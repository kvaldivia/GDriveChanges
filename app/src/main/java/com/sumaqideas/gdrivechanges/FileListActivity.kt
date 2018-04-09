package com.sumaqideas.gdrivechanges

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.drive.model.File

import kotlinx.android.synthetic.main.activity_data.*
import kotlinx.android.synthetic.main.content_data.*

open class FileListActivity :
        AppCompatActivity(), MakeRequestTask.Callback, FileListAdapter.OnItemClickListener {
    lateinit var fileListAdapter: FileListAdapter
    var fileListData: MutableList<File> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        setSupportActionBar(toolbar)
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        fileListAdapter = FileListAdapter(fileListData, this)
        file_list_rv.setHasFixedSize(true)
        file_list_rv.layoutManager = layoutManager
        file_list_rv.adapter = fileListAdapter
        file_list_rv.hasFixedSize()

        file_list_rv.addItemDecoration(
                DividerItemDecoration(file_list_rv.context, layoutManager.orientation))

        pick_file_fab.setOnClickListener { view ->
            MakeRequestTask(SessionManager.credential, this, this).execute()
        }
    }

    override fun onTaskStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClick(item: File) {
        startFileChangesActivity(item)
    }

    override fun onTaskFinished(results: List<File>) {
        fileListData.clear()
        fileListData.addAll(results)
        fileListAdapter.swap(fileListData)
    }

    override fun onTaskFailed(error: Exception) {
        if (error is GooglePlayServicesAvailabilityIOException) {
            SessionManager.showGooglePlayServicesAvailabilityErrorDialog(
                    error.connectionStatusCode
                    , this@FileListActivity)
        } else if (error is UserRecoverableAuthIOException) {
            this@FileListActivity.startActivityForResult(error.intent
                    , SessionManager.REQUEST_AUTHORIZATION)
        } else {

        }
    }

    fun startFileChangesActivity(file: File) {
        var intent = Intent(this, FileChangesActivity::class.java)
        intent.putExtra(FileChangesActivity.PAYLOAD, file.id)
        startActivity(intent)
    }
}

class FileListAdapter(
        var fileList: List<File>, var onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(item: File);
    }
    class FileListViewHolder(var root: LinearLayoutCompat): RecyclerView.ViewHolder(root) {
        var fileNameTextView: TextView = root.findViewById(R.id.file_name_tv)
        var fileIdTextView: TextView  = root.findViewById(R.id.file_id_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FileListViewHolder {
        val rootLayout = LayoutInflater.from(parent?.context)
                .inflate(R.layout.container_file_row, parent, false) as
                LinearLayoutCompat
        return FileListViewHolder(rootLayout)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun swap(data: List<File>) {
        fileList = data
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: FileListViewHolder?, position: Int) {
        var currentFile = fileList.get(position)
        holder!!.fileNameTextView.text = currentFile.name
        holder!!.fileIdTextView.text = currentFile.id
        holder.root.setOnClickListener(View.OnClickListener {
            onItemClickListener.onItemClick(currentFile)
        })
    }
}

