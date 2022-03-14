package ru.vilture.fastsongsterr.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import fastsongsterr.R
import fastsongsterr.databinding.ItemLayoutBinding
import ru.vilture.fastsongsterr.DB.ConnectDB
import ru.vilture.fastsongsterr.MainActivity
import ru.vilture.fastsongsterr.Model.Response
import ru.vilture.fastsongsterr.WebView


class TabAdapter(
    private val context: Context,
    private val name: String,
    tabList: List<Response>,
) : RecyclerView.Adapter<TabAdapter.MyViewHolder>() {

    private var listData: MutableList<Response> = tabList as MutableList<Response>

    class MyViewHolder(itemBinding: ItemLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        val txArtist: TextView = itemBinding.txArtist
        val txSong: TextView = itemBinding.txSong
        val txId: TextView = itemBinding.txId
        val imgGetTab: ImageView = itemBinding.imgGetTab
        val imgFavo: ImageView = itemBinding.imgFavo
        val imgFavoDel: ImageView = itemBinding.imgFavoDel
        val imgShare: ImageView = itemBinding.imgShare
        val card: ConstraintLayout = itemBinding.card

        fun bind(
            context: Context,
            clName: String,
            listItem: Response
        ) {

            txArtist.setOnClickListener {
                txArtist.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animg))
                getTab(
                    context,
                    "http://www.songsterr.com/a/wa/artist?id=" + listItem.artist!!.id,
                    listItem.title.toString()
                )
            }

            imgGetTab.setOnClickListener {
                imgGetTab.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animg))
                getTab(
                    context,
                    "http://www.songsterr.com/a/wa/song?id="
                            + listItem.id + MainActivity().readPref(context),
                    listItem.title.toString()
                )
            }

            imgShare.setOnClickListener {
                imgShare.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animg))
                share(
                    context,
                    "http://www.songsterr.com/a/wa/song?id=" +
                            listItem.id + MainActivity().readPref(context)
                )
            }

            imgFavo.setOnClickListener {
                imgFavo.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animg))

                val values = HashMap<String, String>()
                values["id"] = listItem.id.toString()
                values["artist"] = listItem.artist!!.name.toString()
                values["artistid"] = listItem.artist!!.id.toString()
                values["song"] = listItem.title.toString()

                val resSave = ConnectDB(context).addData(values)
                if (!resSave.isOk)
                    Toast.makeText(context, resSave.message, Toast.LENGTH_SHORT).show()
                else {
                    Toast.makeText(context, context.getString(R.string.okfavo), Toast.LENGTH_SHORT)
                        .show()

                    card.setBackgroundResource(R.color.yellow)
                    imgFavo.visibility = View.GONE
                    imgFavoDel.visibility = View.VISIBLE
                }
            }


            if (ConnectDB(context).existId(listItem.id.toString())) {
                card.setBackgroundResource(R.color.yellow)
                imgFavo.visibility = View.GONE
                imgFavoDel.visibility = View.VISIBLE
            } else {
                imgFavo.visibility = View.VISIBLE
                imgFavoDel.visibility = View.GONE
                card.setBackgroundResource(R.color.white)
            }
        }

        private fun share(context: Context, url: String) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.shareplay))
            shareIntent.putExtra(Intent.EXTRA_TEXT, url)

            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        }

        private fun getTab(context: Context, url: String, song: String) {
            val intent = Intent(context, WebView::class.java)
            intent.putExtra("url", url)
            intent.putExtra("song", song)

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
        holder.bind(context, name, listData[position])

        holder.imgFavoDel.setOnClickListener {
            holder.imgFavoDel.startAnimation(AnimationUtils.loadAnimation(context, R.anim.animg))
            val resDel = ConnectDB(context).delData(listData[holder.adapterPosition].id.toString())
            if (!resDel.isOk)
                Toast.makeText(context, resDel.message, Toast.LENGTH_SHORT).show()
            else {
                listData.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)

                Toast.makeText(context, context.getString(R.string.delFavo), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        holder.txArtist.text = listData[position].artist!!.name
        holder.txSong.text = listData[position].title
        holder.txId.text = listData[position].id.toString()

    }

    override fun getItemCount(): Int {
        return listData.size
    }
}