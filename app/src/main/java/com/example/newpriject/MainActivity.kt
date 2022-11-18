package com.example.newpriject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity  : AppCompatActivity() {

    //------------Buttons and text fields-------------
    private lateinit var intakeTxt: EditText
    private lateinit var intakeBtn: Button
    private lateinit var burnTxt: EditText
    private lateinit var burnBtn: Button
    //------------------------------------------------

    //---------------Shared Preferences--------------
    private lateinit var sharedPreferences: SharedPreferences
    //-----------------------------------------------

    //---------------Recycler Adapter--------------
    private lateinit var CustomAdapter: CustomAdapter
    //---------------------------------------------

    private lateinit var tvPrompt: TextView
    lateinit var undoFAB: FloatingActionButton
    private lateinit var rvMessage: RecyclerView
    private lateinit var transactions: ArrayList<String>
    var total_calories: Int = 0

    //------Limit Flag------
    var intakeLimit = 0
    //----------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactions = ArrayList()

        CustomAdapter = CustomAdapter(transactions)
        rvMessages.adapter = CustomAdapter
        rvMessages.layoutManager = LinearLayoutManager(this)

        get_pref()

        //Getting access through UI
        rvMessage = findViewById(R.id.rvMessages)
        undoFAB = findViewById(R.id.fab_btn)
        tvPrompt = findViewById(R.id.tvPrompt)
        intakeTxt = findViewById(R.id.intake)
        intakeBtn = findViewById(R.id.btnI)
        burnTxt = findViewById(R.id.burn)
        burnBtn = findViewById(R.id.btnB)

        // set click listeners for buttons
        burnBtn.setOnClickListener {
            Burned()
        }
        intakeBtn.setOnClickListener {
            Intake()
        }

        undoFAB.setOnClickListener {
                if (transactions != null && !transactions.isEmpty() ){


                        // Logic to get the last element from ArrayList
                        var lastEntry = transactions[transactions.size - 1]
                        transactions.removeLast()


                        val total_calories_Int = Integer.parseInt(total_calories.toString())

                        // trim everything after delimiter and get it as the substring
                        val last_entry_Int = lastEntry.substringAfter(
                            delimiter = ": ",
                            missingDelimiterValue = "Extension Not found"
                        )

                    total_calories = total_calories_Int - last_entry_Int.toInt()
                    if(total_calories > 0) {
                        Toast.makeText(this@MainActivity, "Undo.", Toast.LENGTH_LONG).show()
                        CustomAdapter.setData(transactions)
                        tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()
                    }else if(total_calories < 0) {

                        total_calories = 0
                        Toast.makeText(this@MainActivity, "Undo.", Toast.LENGTH_LONG).show()
                        CustomAdapter.setData(transactions)
                        tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()
                    }
                }else{
                Log.i("Exception",total_calories.toString())
            }
        }
    }
    private fun Intake(){
        var amount = intakeTxt.text.toString().toInt()
        if(total_calories>=3000 && intakeLimit==0){
            intakeLimit = 1
            total_calories += amount.toInt()
            transactions!!.add("Calories burned: $amount")
            scrollDown()
            CustomAdapter.setData(transactions)
            tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()
            intakeTxt.text.clear()
            intakeTxt.clearFocus()
            Toast.makeText(this@MainActivity, "Daily limit is 3000 calories.", Toast.LENGTH_LONG).show()

            // enter when daily limit is not exceeded
        }else if(total_calories<3000){
            intakeLimit=0
            total_calories += amount.toInt()
            transactions!!.add("Calories burned: $amount")
            scrollDown()
            CustomAdapter.setData(transactions)
            tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()
            intakeTxt.text.clear()
            intakeTxt.clearFocus()
            Toast.makeText(this@MainActivity, "Amount added.", Toast.LENGTH_LONG).show()

            // enter after the second attempt to add intakes after reaching daily calories limit( 3000 )
        }else if(total_calories>=300 && intakeLimit==1){
            tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()
            intakeTxt.text.clear()
            intakeTxt.clearFocus()
            Toast.makeText(this@MainActivity, "Ops!! you cant add more.", Toast.LENGTH_LONG).show()
        }

        //save preferences every time user enter the button
        save_pref()
    }

    private fun Burned(){
        var amount = burnTxt.text.toString().toInt()
        tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()

        if (total_calories > 0) {
            total_calories -= amount.toInt()
            transactions!!.add("Calories intake: $amount")
            scrollDown()
            CustomAdapter.setData(transactions)
            tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()

            burnTxt.text.clear()
            burnTxt.clearFocus()
            Toast.makeText(this@MainActivity, "Amount added.", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this@MainActivity, "You cant burn more calories.", Toast.LENGTH_LONG).show()

            burnTxt.text.clear()
            burnTxt.clearFocus()
        }
        //save preferences every time  user enter the button
        save_pref()
    }

    //=======================================================
    //override onStart & onResume functions to restore states
    override fun onStart() {
        super.onStart()
        tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()
        CustomAdapter.setData(transactions)
    }

    override fun onResume() {
        super.onResume()
        tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()
        CustomAdapter.setData(transactions)
    }//***********************************************************


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("messages",transactions)
        outState.putInt("balance",total_calories)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        total_calories= savedInstanceState.getInt("balance", 0)
      //  transactions= savedInstanceState.putStringArrayList("messages")
        transactions.addAll(savedInstanceState.getStringArrayList("messages")!!)
        rvMessages.adapter?.notifyDataSetChanged()
    }

//******************************************************************************
//******************************************************************************
//Black belt requirements:
//******************************************************************************
//***********************************One****************************************
//******************************************************************************
//Using shared preferences property to store user balance
//save and share preferences:
    fun save_pref(){
        with(sharedPreferences.edit()) {
            putInt("balance", total_calories)

            apply()
        }
    }
    //get user data
    fun get_pref(){

        sharedPreferences = this.getSharedPreferences("itemSP", MODE_PRIVATE)
        total_calories = sharedPreferences.getInt("balance", 0)  // --> retrieves data from Shared Preferences
    }

//******************************************************************************
//***********************************Two****************************************
//******************************************************************************
//Allow users to clear the ledger with a menu option, do not change the balance

    //This function makes the menu appear to the user:
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    //This function specify the action that will happen when the user click on item of the menu:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.option1 -> {

                // removes all elements
                transactions.clear()
                total_calories=0
                tvPrompt.text = "DAILY-CALORIES: " + total_calories.toString()
                CustomAdapter.setData(transactions)
                Toast.makeText(applicationContext, "SELECTED $item from menu", Toast.LENGTH_LONG).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onPrepareOptionsMenu(menu: Menu):Boolean{
        return super.onPrepareOptionsMenu(menu)
    }
    // To Automatically scroll to the bottom of the Recycler View
    private fun scrollDown() {
        rvMessage.smoothScrollToPosition(transactions.size - 1)
    }

}//End of the class

