package co.harismiftahulhudha.otoklixchallenge.helpers

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import co.harismiftahulhudha.otoklixchallenge.R
import co.harismiftahulhudha.otoklixchallenge.databinding.DialogLoadingHelperBinding

class LoadingDialogHelper(val context: Context) {
    private var dialog: Dialog? = null

    fun show(title: String?, message: String?, cancelable: Boolean = false) {
        if (dialog == null) {
            dialog = Dialog(context)
            val binding = DialogLoadingHelperBinding.inflate(LayoutInflater.from(context))
            dialog?.setContentView(binding.root)
            dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.setCancelable(cancelable)
            dialog?.show()

            binding.apply {
                if (title != null) {
                    txtTitle.visibility = View.VISIBLE
                    txtTitle.text = title
                } else {
                    txtTitle.visibility = View.GONE
                }
                if (message != null) {
                    txtMessage.text = message
                } else {
                    txtMessage.text = context.resources.getString(R.string.harap_tunggu)
                }
            }
        }
    }

    fun hide() {
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
    }
}