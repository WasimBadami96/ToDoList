package com.example.todoapplication.ui.deleteallcompleted

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment : DialogFragment(){

    private val viewModel :DeleteAllCompletedViewModel by viewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("confirm Deletion")
            .setMessage("Do You really want to delete all completed task? ")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Yes"){ _, _ ->
                //call viewmodel
                    viewModel.onConfirmClick()
            }
            .create()

}