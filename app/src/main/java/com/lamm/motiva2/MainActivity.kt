package com.lamm.motiva2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lamm.motiva2.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.roundToInt


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity(),PhraseAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding

    private var phraseList : ArrayList<PhraseItem>? = null
    private var allPhrase : ArrayList<PhraseItem>? = null
    private var adapter: PhraseAdapter? = null

    private  var deletePhrase : PhraseItem = PhraseItem(R.drawable.bg,"",false,"none")
    private var currentPhraseImagePos: Int? = null
    private val imageList = arrayListOf(
        R.drawable.bg_1,R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4,
        R.drawable.bg_5,R.drawable.bg_6, R.drawable.bg_7, R.drawable.bg_8,
        R.drawable.bg_9,R.drawable.bg_10,R.drawable.bg_11, R.drawable.bg_12,
        R.drawable.bg_14,R.drawable.bg_15, R.drawable.bg_16,R.drawable.bg_17,
    )


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phraseList = getPhraseList(true)
        allPhrase = getPhraseList(false)

        adapter = PhraseAdapter(phraseList!!,this)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this,0,false)
        binding.recyclerView.setHasFixedSize(true)

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.DOWN.or(ItemTouchHelper.UP)){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            val startPos =  viewHolder.adapterPosition
            val endPos = target.adapterPosition

            Collections.swap(phraseList,startPos,endPos)
            recyclerView.adapter?.notifyItemMoved(startPos,endPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition

            when(direction){
                ItemTouchHelper.UP -> {
                    deletePhrase = phraseList!![pos]
                    phraseList!!.removeAt(pos)
                    adapter?.notifyItemRemoved(pos)
                    insertPhraseItem(pos)
                }

                ItemTouchHelper.DOWN -> {
                    if(deletePhrase.text.isNotEmpty()) {
                        phraseList!!.removeAt(pos)
                        adapter?.notifyItemRemoved(pos)
                        phraseList!!.add(pos,deletePhrase)
                        adapter?.notifyItemInserted(pos)
                    }else{
                        adapter?.notifyItemChanged(pos)
                    }
                }

            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean,
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            val dirUP = 1
            val dirDown  = 2

            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive){
                val bg = ColorDrawable()
                val vItem = viewHolder.itemView

                val dir: Int = when {
                    dY > 0 -> { dirUP }
                    else -> {  dirDown }
                }

                when(dir){
                    dirDown -> {
                        bg.color = Color.DKGRAY
                        bg.setBounds(vItem.left,vItem.top,vItem.right,vItem.bottom)
                        bg.draw(c)
                    }

                    dirUP -> {
                        bg.color = Color.DKGRAY
                        bg.setBounds(vItem.left,vItem.top,vItem.right,vItem.bottom)
                        bg.draw(c)
                    }
                }
            }
        }
    }

    private fun getPhraseList(isSizeOne: Boolean): ArrayList<PhraseItem>{
        val list = ArrayList<PhraseItem>()

        try {
            val obj = JSONObject(getJSONFromAssets()!!)
            val phrasesArray = obj.getJSONArray("phrases")

            if(isSizeOne){
                for (i in 0 until 1) {
                    val imageNumber = getRandomFrom(imageList.size)
                    currentPhraseImagePos = imageNumber
                    val phrase = phrasesArray.getJSONObject(imageNumber)
                    val phText = phrase.getString("text")
                    val phAuthor = phrase.getString("author")
                    val phItem = PhraseItem(imageList[imageNumber],phText,false,phAuthor)
                    list.add(phItem)
                }
            }else{
                for (i in 0 until phrasesArray.length()) {
                    val phrase = phrasesArray.getJSONObject(i)
                    val phText = phrase.getString("text")
                    val phAuthor = phrase.getString("author")
                    val phItem = PhraseItem(R.drawable.bg,phText,false,phAuthor)
                    list.add(phItem)
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return list
    }

    private fun insertPhraseItem(pos: Int){
        val randomPhrasePos = getRandomFrom(allPhrase!!.size)
        val randomImage = getRandomFrom(imageList.size)
        currentPhraseImagePos = randomImage

        val selectedPhrase = allPhrase!![randomPhrasePos]
        selectedPhrase.imageResource = imageList[randomImage]

        phraseList?.add(selectedPhrase)
        adapter?.notifyItemInserted(pos)
    }

    /**
     * Method to load the JSON from the Assets file and return the object
     */
    private fun getJSONFromAssets(): String? {
        val json: String?
        val charset: Charset = Charsets.UTF_8
        try {
            val myPhrasesJSONFile = assets.open("ph.json")
            val size = myPhrasesJSONFile.available()
            val buffer = ByteArray(size)
            myPhrasesJSONFile.read(buffer)
            myPhrasesJSONFile.close()
            json = String(buffer, charset)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun getRandomFrom(range: Int): Int {
        return floor(Math.random() * range).roundToInt()
    }

    override fun onStarButtonClick(pos: Int) {
        val holder = binding.recyclerView.findViewHolderForAdapterPosition(pos)
        addFavPhrase(holder)
        phraseList!![pos].isLike = !phraseList!![pos].isLike
        adapter?.notifyItemChanged(pos)
    }

    override fun onShareButtonClick(pos: Int) {
        val holder = binding.recyclerView.findViewHolderForAdapterPosition(pos)
        val menuContainer: RelativeLayout = holder!!.itemView.findViewById(R.id.menuContainer)
        val actionContainer: RelativeLayout = holder.itemView.findViewById(R.id.actionContainer)
        val ownerTagContainer: RelativeLayout = holder.itemView.findViewById(R.id.ownerTagContainer)
        menuContainer.visibility = View.GONE
        actionContainer.visibility = View.GONE
        ownerTagContainer.visibility = View.VISIBLE

        val imageFile = takeScreenShot(window.decorView.rootView)

        menuContainer.visibility = View.VISIBLE
        actionContainer.visibility = View.VISIBLE
        ownerTagContainer.visibility = View.GONE

        val shareImageIntent = Intent(Intent.ACTION_SEND)
        shareImageIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareImageIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile))
        shareImageIntent.type = "image/png"
        startActivity(Intent.createChooser(shareImageIntent, "Share image via"))
    }

    override fun onMenuButtonClick() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDownloadButtonClick(pos: Int) {
        val holder = binding.recyclerView.findViewHolderForAdapterPosition(pos)
        val menuContainer: RelativeLayout = holder!!.itemView.findViewById(R.id.menuContainer)
        val actionContainer: RelativeLayout = holder.itemView.findViewById(R.id.actionContainer)
        val downloadContainer: RelativeLayout = holder.itemView.findViewById(R.id.downloadContainer)
        val ownerTagContainer: RelativeLayout = holder.itemView.findViewById(R.id.ownerTagContainer)
        menuContainer.visibility = View.GONE
        actionContainer.visibility = View.GONE
        downloadContainer.visibility = View.GONE
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

        menuContainer.visibility = View.VISIBLE
        actionContainer.visibility = View.VISIBLE
        downloadContainer.visibility = View.VISIBLE
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
//            imageFile.setReadable(true, false)
            return imageFile
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun addFavPhrase(holder: RecyclerView.ViewHolder?){
        val phText: TextView = holder!!.itemView.findViewById(R.id.tvPhrase)
        val phAuthor: TextView = holder.itemView.findViewById(R.id.tvAuthor)

        val dbHandler = DatabaseHandler(this)
        val status = dbHandler.addFavPhrase(PhraseModal(0,currentPhraseImagePos.toString(),
            phText.text as String,true, phAuthor.text as String))
        if(status > -1){
            Toast.makeText(this,"Añadida a favorito",Toast.LENGTH_SHORT).show()
        }
    }

}
