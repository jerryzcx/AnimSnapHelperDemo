package com.github.rubensousa.recyclerviewsnap

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.rubensousa.gravitysnaphelper.GravitySnapRecyclerView
import com.github.rubensousa.recyclerviewsnap.adapter.AppAdapter
import com.github.rubensousa.recyclerviewsnap.adapter.SnapListAdapter
import com.github.rubensousa.recyclerviewsnap.model.App
import com.github.rubensousa.recyclerviewsnap.model.SnapList
import java.util.*

class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener {

    companion object {
        const val STATE_ORIENTATION = "orientation"

        fun getApps(): List<App> {
            val apps = ArrayList<App>()
            repeat(2) {
                apps.add(App("Google+", R.drawable.ic_google_48dp, 4.6f))
                apps.add(App("Gmail", R.drawable.ic_gmail_48dp, 4.8f))
                apps.add(App("Inbox", R.drawable.ic_inbox_48dp, 4.5f))
                apps.add(App("Google Keep", R.drawable.ic_keep_48dp, 4.2f))
                apps.add(App("Google Drive", R.drawable.ic_drive_48dp, 4.6f))
                apps.add(App("Hangouts", R.drawable.ic_hangouts_48dp, 3.9f))
                apps.add(App("Google Photos", R.drawable.ic_photos_48dp, 4.6f))
                apps.add(App("Messenger", R.drawable.ic_messenger_48dp, 4.2f))
                apps.add(App("Sheets", R.drawable.ic_sheets_48dp, 4.2f))
                apps.add(App("Slides", R.drawable.ic_slides_48dp, 4.2f))
                apps.add(App("Docs", R.drawable.ic_docs_48dp, 4.2f))
            }
            return apps
        }
    }

    private lateinit var recyclerView: GravitySnapRecyclerView
    private var horizontal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.main)
        toolbar.setOnMenuItemClickListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        horizontal = savedInstanceState?.getBoolean(STATE_ORIENTATION) ?: true

        setupAdapter()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_ORIENTATION, horizontal)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.layoutType) {
            horizontal = !horizontal
            setupAdapter()
            item.title = if (horizontal) "Vertical" else "Horizontal"
        } else if (item.itemId == R.id.grid) {
            startActivity(Intent(this, GridActivity::class.java))
        }
        return false
    }


    private fun setupAdapter() {
        if (horizontal) {
            setupHorizontalAdapter()
        } else {
            setupVerticalAdapter()
        }
    }

    private fun setupHorizontalAdapter() {
        val apps = getApps()
        recyclerView.enableSnapping(false)
        val adapter = SnapListAdapter()
        val lists = listOf(
                SnapList(
                        gravity = Gravity.START,
                        title = "Start",
                        apps = apps
                )
        )
        adapter.setItems(lists)
        recyclerView.adapter = adapter
    }

    private fun setupVerticalAdapter() {
        val adapter = AppAdapter(R.layout.adapter_vertical)
        adapter.setItems(getApps())
        recyclerView.adapter = adapter
        recyclerView.enableSnapping(true)
    }

}
