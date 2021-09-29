package br.pakad_dud.notebuk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.edit_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class EditActivity : AppCompatActivity() {
    val imageCode = 10
    var tempImageUri = "empty"
    var updates = false
    var id_key = 0
    var tempTime = ""


    val myDbManadger = MyDbManadger(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)

        getMyIntents()
    }

    override fun onResume() {
        super.onResume()
        myDbManadger.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManadger.closeDb()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imageCode) {
            imView.setImageURI(data?.data)
            tempImageUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun clickAddImage(view: View) {
        mainimageL.visibility = View.VISIBLE
        fbAddImage1.visibility = View.GONE
    }

    fun delImage(view: View) {
        mainimageL.visibility = View.GONE
        fbAddImage1.visibility = View.VISIBLE
        tempImageUri = "empty"

    }

    fun addImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        //intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, imageCode)
    }

    fun onclickSave(view: View) {
        val myTitle = edTitle.text.toString()
        val myText = edDisk.text.toString()
        val myTime = getCurrentTime()
        if (myTitle != "" && myText != "") {
            CoroutineScope(Dispatchers.Main).launch {
                if (updates) {
                    myDbManadger.updateDb(id_key, myTitle, myText, tempImageUri, tempTime)
                } else {
                    myDbManadger.insertDb(myTitle, myText, tempImageUri, myTime)
                }
                finish() // закрываем Activity
            }

        }
    }

    fun getMyIntents() {
        val i = intent
        if (i != null) {
            if (i.getStringExtra(MyIntentConst.I_TITLE_KEY) != null) {
                updates = true
                edTitle.isEnabled = false
                edDisk.isEnabled = false
                fbEdit.visibility = View.VISIBLE
                fbAddImage1.visibility = View.GONE
                fbSave.visibility = View.GONE
                tempTime = i.getStringExtra(MyIntentConst.I_TIME_KEY).toString()
                edTitle.setText(i.getStringExtra(MyIntentConst.I_TITLE_KEY))
                edDisk.setText(i.getStringExtra(MyIntentConst.I_CONTENT_KEY))
                id_key = i.getIntExtra(MyIntentConst.I_ID_KEY, 0)
                if (i.getStringExtra(MyIntentConst.I_URI_KEY) != "empty") {
                    mainimageL.visibility = View.VISIBLE
                    imView.setImageURI(Uri.parse(i.getStringExtra(MyIntentConst.I_URI_KEY)))
                    tempImageUri = i.getStringExtra(MyIntentConst.I_URI_KEY).toString()

                    ibedit.visibility = View.GONE
                    ibdel.visibility = View.GONE
                }


            }
        }
    }

    fun onClickEditTrue(view: View) {
        edTitle.isEnabled = true
        edDisk.isEnabled = true
        if (tempImageUri == "empty") {
            fbAddImage1.visibility = View.VISIBLE
        }
        fbSave.visibility = View.VISIBLE
        fbEdit.visibility = View.GONE
        ibedit.visibility = View.VISIBLE
        ibdel.visibility = View.VISIBLE
    }
    private fun getCurrentTime(): String{
        val dtime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(dtime)

    }
}