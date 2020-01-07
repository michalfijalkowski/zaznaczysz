package pl.zaznaczysz

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_create_proposition.*
import kotlinx.android.synthetic.main.activity_proposition_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import pl.zaznaczysz.model.Proposition
import pl.zaznaczysz.provider.ActivityProvider
import pl.zaznaczysz.provider.PropositionProvider
import pl.zaznaczysz.provider.UserProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreatePropositionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_proposition)
        loadControls()
    }

    private fun loadControls() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    0
                )
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    0
                )
            }
        }

        imageView.setOnClickListener {
            selectImageInAlbum()
        }

        btnCamera.setOnClickListener {
            takePhoto()
        }

        btnAdd.setOnClickListener {

            if(validate(prop_name) && validate(prop_description)) {
                for (i in 0 until createPropositionLayout.childCount) {
                    val child: View = createPropositionLayout.getChildAt(i)
                    child.setEnabled(false)
                }
                val userId = getIntent().getIntExtra("userId", 0)
                val eventId = getIntent().getIntExtra("eventId", 0)
                doAsync {
                    var proposition = Proposition(
                        0,
                        prop_name.text.toString().trim(),
                        prop_description.text.toString().trim(),
                        "",
                        0,
                        userId,
                        eventId
                    )

                    val addedProposition = PropositionProvider().insertProposition(proposition)

                    uiThread {
                        if (addedProposition.id_user == 0) {
                            toast("Propozycja o podanej nazwie już istnieje")
                            for (i in 0 until createPropositionLayout.childCount) {
                                val child: View = createPropositionLayout.getChildAt(i)
                                child.setEnabled(true)
                            }
                        } else {
                            doAsync {
                                ActivityProvider().updateActivityUser(getIntent().getIntExtra("userId", 0), getIntent().getIntExtra("groupId", 0), 5)
                                UserProvider().updateActivityUser(getIntent().getIntExtra("userId", 0), 5)
                            }
                            toast("Propozycja została dodana")
                            finish()
                        }
                    }
                }

            }else {
                toast("Wypełnij wszytskie pola")
            }

        }
    }

    fun validate(editText: EditText): Boolean {
        if (editText.text.isEmpty())
            return false
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            imageView.setImageURI(data?.data)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            imageView.setImageBitmap(data!!.extras!!.get("data") as Bitmap)
        }
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

//    lateinit var currentPhotoPath: String
//
//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_", /* prefix */
//            ".jpg", /* suffix */
//            storageDir /* directory */
//        ).apply {
//            // Save a file: path for use with ACTION_VIEW intents
//            currentPhotoPath = absolutePath
//        }
//    }

//    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this,
//                        "com.example.android.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
//                }
//            }
//        }
//    }

    fun takePhoto() {
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(packageManager) != null) {
            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
        }
    }

    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }


}
