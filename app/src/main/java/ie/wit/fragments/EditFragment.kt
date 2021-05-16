package ie.wit.fragments


import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ie.wit.R
import ie.wit.main.CarBookingApp
import ie.wit.models.BookingModel
import ie.wit.utils.createLoader
import ie.wit.utils.hideLoader
import ie.wit.utils.showLoader
import kotlinx.android.synthetic.main.fragment_edit.*
import kotlinx.android.synthetic.main.fragment_edit.view.*
import kotlinx.android.synthetic.main.fragment_edit.view.editShowDate
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*

class EditFragment : Fragment(), AnkoLogger {

    lateinit var app: CarBookingApp
    lateinit var loader : AlertDialog
    lateinit var root: View
    var editBooking: BookingModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as CarBookingApp

        arguments?.let {
            editBooking = it.getParcelable("editBooking")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(requireActivity())

        root.editCarBrand.setText(editBooking!!.brand.toString())
        root.editCarModel.setText(editBooking!!.model.toString())
        root.editNumberPlate.setText(editBooking!!.numberPlate.toString())

        setButtonListener(root)

        root.editShowDate.setText(editBooking!!.date.toString())

        root.editBtnAddCar.setOnClickListener {
            showLoader(loader, "Updating Booking on Server...")
            updateBookingData()
            updateBooking(editBooking!!.uid, editBooking!!)
            updateUserBooking(app.currentUser!!.uid,
                editBooking!!.uid, editBooking!!)
        }

        return root
    }

    fun setButtonListener(layout: View){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this.requireActivity(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            editShowDate.text = "$dayOfMonth/$monthOfYear/$year"
        }, year, month, day)

        layout.editBtnGoCalendar.setOnClickListener{
            dpd.show()
        }
    }

    fun updateBookingData(){
        editBooking!!.brand = root.editCarBrand.text.toString()
        editBooking!!.model = root.editCarModel.text.toString()
        editBooking!!.numberPlate = root.editNumberPlate.text.toString()
        editBooking!!.date = root.editShowDate.text.toString()
    }

    fun updateUserBooking(userId: String, uid: String?, booking: BookingModel){
        app.database.child("user-bookings").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(booking)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, BookingListFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Booking error : ${error.message}")
                    }
                })
    }

    fun updateBooking(uid: String?, booking: BookingModel){
        app.database.child("bookings").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(booking)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Booking error : ${error.message}")
                    }
                })
    }


    companion object {
        @JvmStatic
        fun newInstance(booking: BookingModel) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editBooking",booking)
                }
            }
    }
}
