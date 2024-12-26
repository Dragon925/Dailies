package com.github.dragon925.dailies.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.dragon925.dailies.App
import com.github.dragon925.dailies.R
import com.github.dragon925.dailies.data.repository.TaskRepositoryImpl
import com.github.dragon925.dailies.databinding.FragmentTaskListBinding
import com.github.dragon925.dailies.ui.adapters.HorizontalItemDecorator
import com.github.dragon925.dailies.ui.adapters.TaskListAdapter
import com.github.dragon925.dailies.ui.models.TaskListUIEvent
import com.github.dragon925.dailies.ui.models.TaskListUIState
import com.github.dragon925.dailies.ui.models.UIState
import com.github.dragon925.dailies.ui.utils.ViewModelFactory
import com.github.dragon925.dailies.ui.viewmodels.TaskListViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels {
        ViewModelFactory {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            TaskListViewModel(
                repository = TaskRepositoryImpl(
//                    TasksJSONSource,
                    (requireActivity().application as App).database.databaseSource,
                    today
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHideCalendar.setOnClickListener {
            binding.groupCalendar.visibility = View.GONE
            binding.groupCurrentDay.visibility = View.VISIBLE
        }

        binding.btnShowCalendar.setOnClickListener {
            binding.groupCalendar.visibility = View.VISIBLE
            binding.groupCurrentDay.visibility = View.GONE
        }

        binding.calendar.setOnDateChangeListener { _, year, month, day ->
            viewModel.consume(TaskListUIEvent.LoadTasks(LocalDate(year, month + 1, day)))
        }

        binding.fabCreateTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_createTaskFragment)
        }

        binding.daily.adapter = TaskListAdapter(::openTask, ::deleteTask)
        binding.daily.addItemDecoration(HorizontalItemDecorator(requireContext()))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { updateUI(it) }
            }
        }
    }

    private fun updateUI(state: UIState<TaskListUIState>) {
        if (state.isError) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }

        binding.loadingBar.visibility = if (state.isLoading) View.VISIBLE else View.INVISIBLE

        if (state.state == null) return

        (binding.daily.adapter as? TaskListAdapter)?.submitList(state.state.tasks)
        binding.currantDate.text = state.state.date
    }

    private fun openTask(id: Int) {
        findNavController().navigate(
            R.id.action_taskListFragment_to_taskInfoFragment,
            bundleOf("task_id" to id)
        )
    }

    private fun deleteTask(id: Int) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_task_title)
            .setMessage(R.string.confirm_task_delete)
            .setPositiveButton(R.string.confirm) { _, _ ->
                viewModel.consume(TaskListUIEvent.DeleteTask(id))
            }.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}