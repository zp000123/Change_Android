package com.zhang.change.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * <ul>
 * <li>分享任意类型的<b style="color:red">单个</b>文件|不包含目录</li>
 * <li>[<b>经验证！可以发送任意类型的文件！！！</b>]</li>
 * <li># @author http://blog.csdn.net/yuxiaohui78/article/details/8232402</li>
 * <ul>
 *
 * @param context
 * @param uri
 *            Uri.from(file);
 *
 */
fun Context.shareFile(uri: Uri) {   //附件文件地址
    val intent = Intent(Intent.ACTION_SEND)
    intent.putExtra("subject", "") //
    intent.putExtra("body", "") // 正文
    intent.putExtra(Intent.EXTRA_STREAM, uri) // 添加附件，附件为file对象
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (uri.toString().endsWith(".gz")) {
        intent.type = "application/x-gzip" // 如果是gz使用gzip的mime
    } else if (uri.toString().endsWith(".txt")) {
        intent.type = "text/plain" // 纯文本则用text/plain的mime
    } else {
        intent.type = "application/octet-stream" // 其他的均使用流当做二进制数据来发送
    }
    this.startActivity(intent) // 调用系统的mail客户端进行发送
}