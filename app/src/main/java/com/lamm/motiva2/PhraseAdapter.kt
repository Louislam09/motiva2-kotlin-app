package com.lamm.motiva2

import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhraseAdapter(
    private val phraseList: List<PhraseItem>,
    private val listener: OnItemClickListener
    ) :
    RecyclerView.Adapter<PhraseAdapter.PhraseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.phrase_item,
            parent, false
        )

        return PhraseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        val currentItem = phraseList[position]

        holder.phraseContainer.setBackgroundResource(currentItem.imageResource)
        holder.tvPhrase.text = currentItem.text
        holder.tvAuthor.text = "- ${currentItem.author}"

        if (currentItem.isLike) {
            holder.starButton.setBackgroundResource(R.drawable.ic_star_fill_foreground)
        } else {
            holder.starButton.setBackgroundResource(R.drawable.ic_start_foreground)
        }
    }

    override fun getItemCount() = 1

    inner class PhraseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val phraseContainer: RelativeLayout = itemView.findViewById(R.id.phraseContainer)
        val tvPhrase: TextView = itemView.findViewById(R.id.tvPhrase)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        val starButton: Button = itemView.findViewById(R.id.starButton)
        val downloadButton: Button = itemView.findViewById(R.id.downloadButton)
        private val shareButton: Button = itemView.findViewById(R.id.shareButton)
        val menuButton: Button = itemView.findViewById(R.id.menuButton)

        init{
            itemView.setOnClickListener(this)
            starButton.setOnClickListener(this)
            shareButton.setOnClickListener(this)
            menuButton.setOnClickListener(this)
            downloadButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val pos = absoluteAdapterPosition
            when(v?.id){
                R.id.starButton -> {
                    listener.onStarButtonClick(pos)
                }
                R.id.shareButton -> {
                    listener.onShareButtonClick(pos)
                }
                R.id.menuButton -> {
                    listener.onMenuButtonClick()
                }
                R.id.downloadButton -> {
                    listener.onDownloadButtonClick(pos)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onStarButtonClick(pos: Int)
        fun onShareButtonClick(pos: Int)
        fun onMenuButtonClick()
        fun onDownloadButtonClick(pos: Int)
    }

}
