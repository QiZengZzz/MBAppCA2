package ie.wit.fragments


import androidx.appcompat.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ie.wit.R
import ie.wit.main.CarBookingApp
import ie.wit.models.BookingModel
import ie.wit.utils.*
import kotlinx.android.synthetic.main.fragment_booking.*
import kotlinx.android.synthetic.main.fragment_booking.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.toast
import java.util.*


class BookingFragment : Fragment(), AnkoLogger {

    var valet = BookingModel()
    var edit = false
    var favourite = false
    lateinit var app: CarBookingApp
    lateinit var loader : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as CarBookingApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_booking, container, false)
        loader = createLoader(requireActivity())
        activity?.title = getString(R.string.action_book)

        setButtonListener(root)
        setFavouriteListener(root)
        return root
    }

    fun setButtonListener(layout: View){
        //Date Picker (https://stackoverflow.com/questions/45842167/how-to-use-datepickerdialog-in-kotlin#45844018)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this.requireActivity(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            // Display Selected date in text box
            showDate.text = "$dayOfMonth/$monthOfYear/$year"
        }, year, month, day)

        layout.btnGoCalendar.setOnClickListener{
            dpd.show()
        }

        layout.btnAddCar.setOnClickListener{

            val brand = layout.carBrand.text.toString()
            val model = layout.carModel.text.toString()
            val plate = layout.numberPlate.text.toString()
            val date = layout.showDate.text.toString()

            if(valet.brand.isEmpty()){
                toast("Please enter a car")
            }else {
                if (edit) {
                    //app.valets.update(valet.copy())
                } else {
                    writeNewBooking(BookingModel(brand = brand,
                        model = model,
                        numberPlate = plate,
                        date = date,
                        profilepic = app.userImage.toString(),
                        isfavourite = favourite,
                        latitude = app.currentLocation.latitude,
                        longitude = app.currentLocation.longitude,
                        email = app.currentUser?.email))
                }
            }
        }
    }

    fun setFavouriteListener (layout: View) {
        layout.imageFavourite.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (!favourite) {
                    layout.imageFavourite.setImageResource(android.R.drawable.star_big_on)
                    favourite = true
                }
                else {
                    layout.imageFavourite.setImageResource(android.R.drawable.star_big_off)
                    favourite = false
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BookingFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun writeNewBooking(booking: BookingModel){
        // Create new booking at /bookings & /bookings/$uid
        showLoader(loader, "Adding booking to Firebase")
        info("Firebase DB reference : ${app.database}")
        val uid = app.currentUser!!.uid
        val key = app.database.child("bookings").push().key
        if(key == null){
            info("Firebase Error : Key Empty")
            return
        }
        booking.uid = key
        val bookingValues = booking.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/bookings/$key"] = bookingValues
        childUpdates["/user-bookings/$uid/$key"] = bookingValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }
}
