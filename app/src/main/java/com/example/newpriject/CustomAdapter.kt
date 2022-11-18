package com.example.newpriject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(var transactions : ArrayList<String>)
    : RecyclerView.Adapter<CustomAdapter.TransactionHolder>() {

    class TransactionHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val amount: TextView = itemView.findViewById(R.id.amount)

}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_design, parent, false)
        return TransactionHolder(view)
    }
    //onBindViewHolder: display the data at the specified position
    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {

        var transaction = transactions[position]

        holder.amount.text = "$transaction"

        }
    override fun getItemCount() = transactions.size

    //update list
    fun setData(newTransactions: ArrayList<String>){
        transactions = newTransactions
        notifyDataSetChanged()
    }
}

