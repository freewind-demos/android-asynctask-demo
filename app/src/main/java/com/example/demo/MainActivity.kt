package com.example.demo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar

    // 继承 AsyncTask
    // 第一个泛型：doInBackground 的参数类型
    // 第二个泛型：onProgressUpdate 的参数类型
    // 第三个泛型：onPostExecute 的参数类型
    private inner class MyAsyncTask : AsyncTask<Int, Int, String>() {

        // 在后台任务执行前调用，运行在主线程
        // 通常用于显示加载提示
        override fun onPreExecute() {
            super.onPreExecute()
            textView.text = "开始下载..."
        }

        // 在后台执行，运行在子线程
        // 这里执行耗时的操作，如网络请求、文件下载等
        override fun doInBackground(vararg params: Int?): String {
            val total = params[0] ?: 10

            for (i in 1..total) {
                try {
                    Thread.sleep(500)  // 模拟耗时操作
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                // 发布进度，会调用 onProgressUpdate
                publishProgress(i * 100 / total)
            }

            return "下载完成!"
        }

        // 更新进度，运行在主线程
        // 当调用 publishProgress 时会触发
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            val progress = values[0] ?: 0
            progressBar.progress = progress
            textView.text = "下载进度: $progress%"
        }

        // 后台任务完成后调用，运行在主线程
        // 可以更新 UI
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            textView.text = result ?: "完成"
            progressBar.progress = 100
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)

        // 执行 AsyncTask，传入总步数
        MyAsyncTask().execute(10)
    }
}
