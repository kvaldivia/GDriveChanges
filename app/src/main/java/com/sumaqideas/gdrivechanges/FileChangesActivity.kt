package com.sumaqideas.gdrivechanges

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment

import kotlinx.android.synthetic.main.activity_file_changes.*

class FileChangesActivity : AppCompatActivity() {
    companion object {
        val PAYLOAD: String = "payload"
    }

    lateinit var fileChangesFragment: Fragment

    var fileId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_changes)
        setSupportActionBar(toolbar)
        fileChangesFragment = supportFragmentManager
            .findFragmentById(R.id.file_changes_fragment)
        fileId = savedInstanceState?.getString(PAYLOAD)
    }


    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        if (fragment is FileChangesActivityFragment) {
            fragment.fileId = fileId
        }
    }
}
