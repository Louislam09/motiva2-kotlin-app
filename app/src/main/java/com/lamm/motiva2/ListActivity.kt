package com.lamm.motiva2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lamm.motiva2.databinding.ActivityListBinding
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class ListActivity : AppCompatActivity(),ListAdapter.OnItemClickListener {
    private lateinit var binding: ActivityListBinding

    private var phraseList : ArrayList<PhraseModal>? = null
    private var adapter: ListAdapter? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phraseList = getPhraseFromDB()

        adapter = ListAdapter(phraseList!!,this)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this,1,false)
        binding.recyclerView.setHasFixedSize(true)

        if(adapter!!.itemCount == 0) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE

            binding.emptyBackButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }

    }

    private fun getPhraseFromDB(): ArrayList<PhraseModal>{
        val dbHandler = DatabaseHandler(this)
        return dbHandler.viewPhrases()
    }

    private fun takeScreenShot(view: View): File? {
        try {
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            val imageFile = File(externalCacheDir, "phraseImage.png")
            val fOut = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            return imageFile
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStarButtonClick(pos: Int) {
        val dbHandler = DatabaseHandler(this)
        dbHandler.deleteFavPhrase(phraseList!![pos].id)
        phraseList?.remove(phraseList!![pos])
        adapter?.notifyItemRemoved(pos)
        adapter?.notifyDataSetChanged()

        if(adapter!!.itemCount == 0) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE

            binding.emptyBackButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onBackButtonClick(pos: Int) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onShareButtonClick(pos: Int) {
        val holder = binding.recyclerView.findViewHolderForAdapterPosition(pos)
        val favMenuContainer: RelativeLayout = holder!!.itemView.findViewById(R.id.favMenuContainer)
        val favActionContainer: RelativeLayout = holder.itemView.findViewById(R.id.favActionContainer)
        val favDownloadContainer: RelativeLayout = holder.itemView.findViewById(R.id.favDownloadContainer)
        val favShareContainer: RelativeLayout = holder.itemView.findViewById(R.id.favShareContainer)
        val ownerTagContainer: RelativeLayout = holder.itemView.findViewById(R.id.ownerTagContainer)

        // Hide the icon before the screen shot
        favMenuContainer.visibility = View.GONE
        favActionContainer.visibility = View.GONE
        favDownloadContainer.visibility = View.GONE
        favShareContainer.visibility = View.GONE
        ownerTagContainer.visibility = View.VISIBLE

        val imageFile = takeScreenShot(window.decorView.rootView)

        // show the container icon that were hidden
        favMenuContainer.visibility = View.VISIBLE
        favActionContainer.visibility = View.VISIBLE
        favDownloadContainer.visibility = View.VISIBLE
        favShareContainer.visibility = View.VISIBLE
        ownerTagContainer.visibility = View.GONE

        // Intent to share the image
        val shareImageIntent = Intent(Intent.ACTION_SEND)
        shareImageIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareImageIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile))
        shareImageIntent.type = "image/png"
        startActivity(Intent.createChooser(shareImageIntent, "Share image via"))
    }

    override fun onDownloadButtonClick(pos: Int) {
        val holder = binding.recyclerView.findViewHolderForAdapterPosition(pos)
        val favMenuContainer: RelativeLayout = holder!!.itemView.findViewById(R.id.favMenuContainer)
        val favActionContainer: RelativeLayout = holder.itemView.findViewById(R.id.favActionContainer)
        val favDownloadContainer: RelativeLayout = holder.itemView.findViewById(R.id.favDownloadContainer)
        val favShareContainer: RelativeLayout = holder.itemView.findViewById(R.id.favShareContainer)
        val ownerTagContainer: RelativeLayout = holder.itemView.findViewById(R.id.ownerTagContainer)

        // Hide the icon before the screen shot
        favMenuContainer.visibility = View.GONE
        favActionContainer.visibility = View.GONE
        favDownloadContainer.visibility = View.GONE
        favShareContainer.visibility = View.GONE
        ownerTagContainer.visibility = View.VISIBLE

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100)
            }else{
                saveImage(window.decorView.rootView)
            }
        }else{
            saveImage(window.decorView.rootView)
        }

        // show the container icon that were hidden
        favMenuContainer.visibility = View.VISIBLE
        favActionContainer.visibility = View.VISIBLE
        favDownloadContainer.visibility = View.VISIBLE
        favShareContainer.visibility = View.VISIBLE
        ownerTagContainer.visibility = View.GONE

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // save image here
                // saveImage(window.decorView.rootView,"Phrase",pos)

            }else {
                Toast.makeText(this,"Permission not granted, so image can't be saved in storage.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to save an image
    private fun saveImage(view: View){
        val externalStorageState = Environment.getExternalStorageState()
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)){
            val date = Date().time
            val storageDir = Environment.getExternalStorageDirectory().toString()

            try {
                val myDir = File("$storageDir/Motiva2/")
                myDir.mkdirs()
                val imageFile = File(myDir, "frase$date.png")
                val fOut = FileOutputStream(imageFile)

                view.isDrawingCacheEnabled = true
                val bitmap = Bitmap.createBitmap(view.drawingCache)
                view.isDrawingCacheEnabled = false

//                MediaStore.Images.Media.insertImage(
//                    contentResolver,
//                    bitmap,
//                    "frase$date",
//                    "Phrase image from motiva2 app"
//                )

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.flush()
                fOut.close()

                Toast.makeText(this,"Guardada en: ${Uri.parse(imageFile.absolutePath)}",
                    Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }else{
            Toast.makeText(this,"No Fue Posible Acceder Al Almacenamiento", Toast.LENGTH_SHORT).show()
        }

    }


}