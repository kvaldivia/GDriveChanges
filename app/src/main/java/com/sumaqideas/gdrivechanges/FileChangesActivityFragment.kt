package com.sumaqideas.gdrivechanges

import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.api.services.drive.model.File


class FileChangesActivityFragment : Fragment() {
    var fileId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (fileId != null) {
            Log.d("FileChangesFragment", "off we go");
        }
        return inflater.inflate(R.layout.fragment_file_changes, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
}


class FileChangesAdapter() : RecyclerView.Adapter<FileChangesAdapter.FileChangesViewHolder>() {
    inner class FileChangesViewHolder(itemView: ConstraintLayout) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FileChangesAdapter.FileChangesViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: FileChangesViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
