package ru.vilture.fastsongsterr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fastsongsterr.databinding.ItemLayoutBinding
import ru.vilture.fastsongsterr.Model.Response


class TabAdapter(
    private val context: Context,
    private val tabList: List<Response>,
) : RecyclerView.Adapter<TabAdapter.MyViewHolder>() {

    class MyViewHolder(itemBinding: ItemLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        val txArtist: TextView
        val txSong: TextView
        val txId: TextView
        val txGetTabP: ImageView

        fun bind(context: Context,listItem: Response) {
            txGetTabP.setOnClickListener {
                getTab(context, "http://www.songsterr.com/a/wa/song?id=" + listItem.id)

                Log.d("retrofit", "http://www.songsterr.com/a/wa/song?id=" + listItem.id)
                }
            }

        init {

            this.txArtist = itemBinding.txArtist
            this.txSong = itemBinding.txSong
            this.txId = itemBinding.txId
            this.txGetTabP = itemBinding.getTabP
        }

        private fun getTab(context: Context, url: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemBinding =
            ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listItem = tabList[position]
        val context = context
        holder.bind(context,listItem)

        // Picasso.get().load(tabList[position].imageurl).into(holder.image)
        holder.txArtist.text = tabList[position].artist!!.name
        holder.txSong.text = tabList[position].title
        holder.txId.text = tabList[position].id.toString()
    }

    override fun getItemCount(): Int {
        return tabList.size
    }
}