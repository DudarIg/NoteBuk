package br.pakad_dud.notebuk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val myDbManadger = MyDbManadger(this)
    val myAdapter = MyAdapter(ArrayList(), this)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // инициализация адаптера
        rcView.layoutManager = LinearLayoutManager(this)
        val swapHelper = getSwapMg()                        // подсоединяем обработчик свайпа
        swapHelper.attachToRecyclerView(rcView)             // к rcView
        initSearchView()                                   // инициализация поиска searchView

        rcView.adapter = myAdapter


    }

    override fun onResume() {
        super.onResume()
        myDbManadger.openDb()
        fillAdapter("")
        tvNoElevtnts.visibility = if (myAdapter.itemCount != 0)  View.GONE else View.VISIBLE
        searchView.onActionViewCollapsed()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManadger.closeDb()
    }
    // добавление записей в БД
    fun onClickNew(view: View){
        val intent = Intent(this, EditActivity::class.java )
        startActivity(intent)
    }
    // удаление записей из БД свайп
    private fun getSwapMg(): ItemTouchHelper {
        return ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            // перетаскивание не задействуем
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            // задействуем свайп
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManadger)
            }
        })
    }

    private fun fillAdapter(text:String){
        // обновление адаптера через корутыны
        job?.cancel() // перезапускаем карутину
        job = CoroutineScope(Dispatchers.Main).launch {
            val list = myDbManadger.readDb(text)
            myAdapter.updateAdapter(list)

        }

    }

    fun initSearchView(){
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
//                val list = myDbManadger.readDb(text!!)
//                myAdapter.updateAdapter(list)
                fillAdapter(text!!)
                return true
            }
        } )
    }
}