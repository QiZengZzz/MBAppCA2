package ie.wit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso
import ie.wit.R
import ie.wit.fragments.ListAllFragment
import ie.wit.models.BookingModel
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.card_valet.view.*

interface ValetingListener{
    fun onValetClick(booking: BookingModel)
}

class ValetingAdapter(options: FirebaseRecyclerOptions<BookingModel>,
                      private val listener: ValetingListener?)
    : FirebaseRecyclerAdapter<BookingModel,
        ValetingAdapter.ValetViewHolder>(options) {

    class ValetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(booking: BookingModel, listener: ValetingListener) {
            with(booking) {
                itemView.tag = booking
                itemView.carBrandDisplay.text = booking.brand
                itemView.carModelDisplay.text = booking.model
                itemView.licensePlateDisplay.text = booking.numberPlate
                itemView.dateShown.text = booking.date
                if(booking.isfavourite) itemView.imagefavourite.setImageResource(android.R.drawable.star_big_on)

                if(listener is ListAllFragment)
                    ; // Do Nothing, Don't Allow 'Clickable' Rows
                else
                    itemView.setOnClickListener { listener.onValetClick(booking) }

                if(booking.isfavourite) itemView.imagefavourite.setImageResource(android.R.drawable.star_big_on)

                if(!booking.profilepic.isEmpty()) {
                    Picasso.get().load(booking.profilepic.toUri())
                        //.resizevalet
                        .transform(CropCircleTransformation())
                        .into(itemView.imageIcon)
                }
                else
                    itemView.imageIcon.setImageResource(R.mipmap.ic_launcher_darthvadar)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValetViewHolder {

        return ValetViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_valet, parent, false))
    }

    override fun onBindViewHolder(holder: ValetViewHolder, position: Int, model: BookingModel) {
        holder.bind(model,listener!!)
    }


}