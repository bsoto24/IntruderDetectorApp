package pe.openlab.sistemasdistribuidos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {


    var sistema: Boolean? = false
    var intruso: Boolean? = false
    var launched: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val database = FirebaseDatabase.getInstance()
        val refSistema = database.getReference("sistema")
        val refSistemaFlag = refSistema.child("flag")
        val refIntruso = database.getReference("intruso")
        val refIntrusoFlag = refIntruso.child("flag")

        refSistemaFlag.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sistema = dataSnapshot.getValue(Boolean::class.java)
                switch_view.setOn(sistema!!, true)
                if(sistema!!){
                    tvAlarma.text = "Alarma activada"
                }else{
                    tvAlarma.text = "Alarma desactivada"
                }
                if (launched){
                    viewAlarma.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    launched = false
                }
                Log.e("Firebase", "Sistema: " + sistema!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })

        refIntrusoFlag.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (sistema!!){
                    intruso = dataSnapshot.getValue(Boolean::class.java)
                    if(intruso!!){
                        tvIntruso.visibility = View.VISIBLE
                    }else{
                        tvIntruso.visibility = View.GONE
                    }
                    Log.e("Firebase", "Intruso: " + intruso!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })

        switch_view.setOnSwitchStateChangeListener {
            refSistemaFlag.setValue(switch_view.isOn)
            tvIntruso.visibility = View.GONE
            refIntrusoFlag.setValue(false)
            if(switch_view.isOn){
                tvAlarma.text = "Alarma activada"
            }else{
                tvAlarma.text = "Alarma desactivada"
            }
        }
    }
}
