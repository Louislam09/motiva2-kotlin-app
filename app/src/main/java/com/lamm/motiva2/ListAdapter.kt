package com.lamm.motiva2

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


@Suppress("DEPRECATION")
class ListAdapter(
    private val phraseList: List<PhraseModal>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<ListAdapter.PhraseViewHolder>() {
    var lastPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.fav_phrase,
            parent, false
        )

        return PhraseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        val currentItem = phraseList[position]
        println("Position : " + position)

        val imageList = arrayListOf(
            R.drawable.bg_1,R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4,
            R.drawable.bg_5,R.drawable.bg_6, R.drawable.bg_7, R.drawable.bg_8,
            R.drawable.bg_9,R.drawable.bg_10,R.drawable.bg_11, R.drawable.bg_12,
            R.drawable.bg_14,R.drawable.bg_15, R.drawable.bg_16,R.drawable.bg_17,
        )

        if(position != 0){
            holder.favMenuContainer.visibility = View.INVISIBLE
        }

        holder.favPhraseContainer.setBackgroundResource(imageList[currentItem.imageResource.toInt() ])
        holder.favTvPhrase.text = currentItem.text
        holder.favTvAuthor.text = "- ${currentItem.author}"

        if (currentItem.isLike) {
            holder.favStartButton.setBackgroundResource(R.drawable.ic_star_fill_foreground)
        } else {
            holder.favStartButton.setBackgroundResource(R.drawable.ic_start_foreground)
        }
    }

    override fun getItemCount() = phraseList.size

    inner class PhraseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val favPhraseContainer: RelativeLayout = itemView.findViewById(R.id.favPhraseContainer)
        val favMenuContainer: RelativeLayout = itemView.findViewById(R.id.favMenuContainer)
        val favTvPhrase: TextView = itemView.findViewById(R.id.favTvPhrase)
        val favTvAuthor: TextView = itemView.findViewById(R.id.favTvAuthor)
        val favStartButton: Button = itemView.findViewById(R.id.favStartButton)
        val favShareButton: Button = itemView.findViewById(R.id.favShareButton)
        val favDownloadButton: Button = itemView.findViewById(R.id.favDownloadButton)
        private val favBackButton: Button = itemView.findViewById(R.id.favBackButton)

        init {
            itemView.setOnClickListener(this)
            favStartButton.setOnClickListener(this)
            favBackButton.setOnClickListener(this)
            favDownloadButton.setOnClickListener(this)
            favShareButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val pos = absoluteAdapterPosition

            when(v?.id){
                R.id.favStartButton -> {
                    listener.onStarButtonClick(pos)
                }
                R.id.favBackButton -> {
                    listener.onBackButtonClick(pos)
                }
                R.id.favShareButton -> {
                    listener.onShareButtonClick(pos)
                }
                R.id.favDownloadButton -> {
                    listener.onDownloadButtonClick(pos)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onStarButtonClick(pos: Int)
        fun onBackButtonClick(pos: Int)
        fun onShareButtonClick(pos: Int)
        fun onDownloadButtonClick(pos: Int)
    }

}
