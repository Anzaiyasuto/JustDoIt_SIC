package sic.example.justdoit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testnotificationmanagerrepeat.R
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

/**
 * アプリのメイン画面の処理.
 *
 * @author 安斎康人
 * @version 0721
 *
 */

/**
 * アプリのメイン画面のクラスです.
 * @author 安斎康人
 * @version 0721
 */
class MainActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    /**
     * MainActivityの初期化等を行うメソッドです.
     *@param realm(Realm)
     *@return void
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

    }

    /**
     *MainActivity開始時に呼ばれるメソッドです.
     *@param layout(LinearLayoutManager),
     *
     */
    override fun onStart() {
        super.onStart()
        realm = Realm.getDefaultInstance()
        recycleView.layoutManager = LinearLayoutManager(this)
        val layout = LinearLayoutManager(this@MainActivity)

        val query = realm.where<Schedule>()
        query.equalTo("completeFlag", 0.toInt())
        val schedules = query.findAll()

        val adapter = ScheduleAdapter(schedules)
        recycleView.adapter = adapter

        //区切り専用のオブジェクトを生成。
        val decorator = DividerItemDecoration(this@MainActivity, layout.orientation)
        //RecyclerViewに区切り線オブジェクトを設定。
        recycleView.addItemDecoration(decorator)
        fab.setOnClickListener {
            val intent = Intent(this, ScheduleEditActivity::class.java)
            startActivity(intent)
        }
        adapter.setOnItemClickListener { id ->
            val intent1 = Intent(this, ScheduleEditActivity::class.java)
                .putExtra("schedule_id", id)
            startActivity(intent1)
        }
        adapter.setOnItemLongClickListener{ id ->
            val intent2 = Intent(this, CompleteTaskActivity::class.java)
                .putExtra("schedule_id", id)
            startActivity(intent2)
        }

    }

    /**
     *MainActivityの終了をするメソッドです.
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
