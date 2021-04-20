package com.techdoctorbd.taskmanagermongodb.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.techdoctorbd.taskmanagermongodb.adapters.TaskListAdapter
import com.techdoctorbd.taskmanagermongodb.data.models.Task
import com.techdoctorbd.taskmanagermongodb.databinding.ActivityMainBinding
import com.techdoctorbd.taskmanagermongodb.ui.tasks.AddTaskActivity
import com.techdoctorbd.taskmanagermongodb.utils.NoScrollListView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        binding.tvAddNewTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        mainViewModel.profileResponse.observe(this, {
            if (it.message.isNullOrEmpty()) {
                binding.toolbarTitle.text = "Welcome ${it.data?.name}!"
            } else
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        })

        mainViewModel.taskListResponse.observe(this, {
            if (it.message.isNullOrEmpty()) {
                binding.layoutWelcome.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
            } else
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        })

        mainViewModel.pendingTaskList.observe(this, {
            setDataToList(binding.pendingContainer, binding.taskListPending, it)
        })

        mainViewModel.todayTaskList.observe(this, {
            setDataToList(binding.todayContainer, binding.taskListToday, it)
        })

        mainViewModel.tomorrowsTaskList.observe(this, {
            setDataToList(binding.tomorrowContainer, binding.taskListTomorrow, it)
        })

        mainViewModel.upcomingTaskList.observe(this, {
            setDataToList(binding.upcomingContainer, binding.taskListUpcoming, it)
        })
    }

    private fun setDataToList(
        layout: LinearLayout,
        listView: NoScrollListView,
        taskList: List<Task>
    ) {
        layout.visibility = View.VISIBLE
        val taskListAdapter = TaskListAdapter(taskList, this)
        listView.adapter = taskListAdapter
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getTasks(HashMap())
    }
}