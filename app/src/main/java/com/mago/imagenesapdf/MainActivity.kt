package com.mago.imagenesapdf

import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        val imagesFolder = arrayListOf<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //getFile(Environment.getExternalStorageDirectory())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getFile(dir: File) {
        val listFile = dir.listFiles()
        if (listFile != null && listFile.isNotEmpty()) {
            for (file in listFile) {
                if (file.isDirectory) {
                    getFile(file)
                } else {
                    if (file.name.endsWith(".png")
                        || file.name.endsWith(".jpg")
                        || file.name.endsWith(".jpeg")
                        || file.name.endsWith(".gif")
                        || file.name.endsWith(".bmp")
                        || file.name.endsWith(".webp")
                    ) {
                        val temp: String =
                            file.path.substring(0, file.path.lastIndexOf('/'))
                        if (!imagesFolder.contains(temp)) imagesFolder.add(temp)
                    }
                }
            }
        }

    }

}
