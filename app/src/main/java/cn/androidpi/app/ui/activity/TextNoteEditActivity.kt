package cn.androidpi.app.ui.activity

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import cn.androidpi.app.R
import cn.androidpi.app.databinding.ActivityTextNoteEditBinding
import cn.androidpi.app.ui.base.BaseActivity
import cn.androidpi.app.ui.base.BindLayout
import cn.androidpi.app.viewmodel.TextNoteEditViewModel
import cn.androidpi.app.viewmodel.ViewModelFactory
import javax.inject.Inject

@BindLayout(R.layout.activity_text_note_edit)
class TextNoteEditActivity : BaseActivity() {

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory

    lateinit var mTextNoteEditViewModel: TextNoteEditViewModel

    lateinit var mBinding: ActivityTextNoteEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_text_note_edit)
        mTextNoteEditViewModel = mViewModelFactory.create(TextNoteEditViewModel::class.java)

        mBinding.etContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mTextNoteEditViewModel.updateText(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        mBinding.btnCommit.setOnClickListener { v ->
            if (mTextNoteEditViewModel.isValid()) {
                mTextNoteEditViewModel.saveTextNote()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Snackbar.make(v, "笔记内容为空", Snackbar.LENGTH_SHORT).show()
            }
        }

    }
}
