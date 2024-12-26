package com.github.dragon925.dailies.ui.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.dragon925.dailies.App
import com.github.dragon925.dailies.R
import com.github.dragon925.dailies.data.repository.TaskRepositoryImpl
import com.github.dragon925.dailies.databinding.FragmentTaskInfoBinding
import com.github.dragon925.dailies.ui.models.TaskInfoUIEvent
import com.github.dragon925.dailies.ui.models.TaskInfoUIState
import com.github.dragon925.dailies.ui.models.UIState
import com.github.dragon925.dailies.ui.utils.ViewModelFactory
import com.github.dragon925.dailies.ui.viewmodels.TaskInfoViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn


private const val TASK_ID = "task_id"

class TaskInfoFragment : Fragment() {
    private var taskId: Int? = null
    private var _binding: FragmentTaskInfoBinding? = null

    private val binding get() = _binding!!

    private val viewModel: TaskInfoViewModel by viewModels {
        ViewModelFactory {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            TaskInfoViewModel(
                repository = TaskRepositoryImpl(
//                    TasksJSONSource,
                    (requireActivity().application as App).database.databaseSource,
                    today
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getInt(TASK_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.description.movementMethod = ScrollingMovementMethod()
        setupToolbar()

        viewModel.consume(TaskInfoUIEvent.LoadTask(taskId ?: -1))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { updateUI(it) }
            }
        }

    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_delete -> {
                        showDeleteDialog()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun updateUI(state: UIState<TaskInfoUIState>) {
        if (state.isError) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }

        binding.loadingBar.visibility = if (state.isLoading) View.VISIBLE else View.INVISIBLE

        val task = state.state ?: return
        with(binding) {
            name.text = task.name
            dateStart.text = task.dateStart
            dateEnd.text = task.dateEnd
            description.text = task.description
        }
    }

    private fun showDeleteDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_task_title)
            .setMessage(R.string.confirm_task_delete)
            .setPositiveButton(R.string.confirm) { _, _ ->
                viewModel.consume(TaskInfoUIEvent.DeleteTask(taskId ?: -1))
                findNavController().navigateUp()
            }.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}