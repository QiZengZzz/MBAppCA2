package ie.wit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import ie.wit.R
import ie.wit.adapters.ValetingAdapter
import ie.wit.adapters.ValetingListener
import ie.wit.models.BookingModel
import kotlinx.android.synthetic.main.fragment_booking_list.view.*


class ListAllFragment : BookingListFragment(),
    ValetingListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_booking_list, container, false)
        activity?.title = getString(R.string.menu_report_all)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))

        var query = FirebaseDatabase.getInstance()
            .reference.child("bookings")

        var options = FirebaseRecyclerOptions.Builder<BookingModel>()
            .setQuery(query, BookingModel::class.java)
            .setLifecycleOwner(this)
            .build()

        root.recyclerView.adapter = ValetingAdapter(options, this)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ListAllFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}
