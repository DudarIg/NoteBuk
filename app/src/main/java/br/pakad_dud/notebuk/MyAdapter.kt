package br.pakad_dud.notebuk

import android.content.Context
import android.content.Intent
import android.graphics.Color.parseColor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(listMain: ArrayList<ListBuk>, contextM: Context) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var listArray = listMain
    var context = contextM


    class MyHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView) {

        val tvTitle : TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime : TextView = itemView.findViewById(R.id.tvTime)
        val context: Context = contextV

        fun setData(listBuk: ListBuk){

            if (listBuk.uri != "empty") {
                tvTitle.setTextColor(parseColor("#FFFFFF"))
            }
            else {
                tvTitle.setTextColor(parseColor("#CECECF"))
            }
            tvTitle.text = listBuk.title
            tvTime.text = listBuk.time
            // обработчик нажатия на строку RecyclerViev
            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                        putExtra(MyIntentConst.I_ID_KEY, listBuk.id)
                        putExtra(MyIntentConst.I_TITLE_KEY, listBuk.title)
                        putExtra(MyIntentConst.I_CONTENT_KEY, listBuk.content)
                        putExtra(MyIntentConst.I_URI_KEY, listBuk.uri)
                        putExtra(MyIntentConst.I_TIME_KEY, listBuk.time)
                }
                context.startActivity(intent)
            }
        }
    }

    // готовим шаблон для рисования
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context) // спецкласс для надувания rc_shablon.xml
        val myInflater = inflater.inflate(R.layout.rc_shablon,parent, false)
        return MyHolder(myInflater, context)
    }
    // количество элементов в списке
    override fun getItemCount(): Int {
          return listArray.size
    }
    // подключает данные с позиции массива к нарисованному шаблону
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray.get(position))
    }
    // обновление адаптера
    fun updateAdapter(listItems: List<ListBuk>){
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }
    // удаление элемента списка из адаптера
    fun removeItem(pos: Int, dbManadger: MyDbManadger){
        dbManadger.removeItemDb(listArray[pos].id.toString()) // удаление записи из БД
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}