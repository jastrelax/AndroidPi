package cn.androidpi.app.viewmodel

import android.arch.lifecycle.ViewModel
import cn.androidpi.data.repository.TextNoteRepo
import cn.androidpi.note.entity.TextNote
import cn.androidpi.note.model.TextNoteEditModel
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by jastrelax on 2017/12/7.
 */
class TextNoteEditViewModel @Inject constructor() : ViewModel(), TextNoteEditModel {

    @Inject
    lateinit var mTextNoteRepo: TextNoteRepo

    var mTextNote = TextNote()

    override fun saveTextNote() {
        mTextNoteRepo.addTextNote(mTextNote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable?) {
                    }

                    override fun onError(e: Throwable?) {
                    }
                })
    }

    fun updateText(content: String) {
        mTextNote.text = content
    }

    // Validity checking should be performed in view or view-model?
    fun isValid(): Boolean {
        return !mTextNote.text.isNullOrBlank()
    }
}