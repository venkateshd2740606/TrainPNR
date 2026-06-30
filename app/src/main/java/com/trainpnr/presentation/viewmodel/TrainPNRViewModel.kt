package com.trainpnr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainpnr.domain.model.SavedPnr
import com.trainpnr.domain.model.UserPreferences
import com.trainpnr.domain.repository.PreferencesRepository
import com.trainpnr.domain.repository.SavedPnrRepository
import com.trainpnr.engine.PnrEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainPNRViewModel @Inject constructor(
    private val savedPnrRepository: SavedPnrRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val savedPnrs = savedPnrRepository.observeSaved()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val preferences = preferencesRepository.getUserPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    private val _pnrInput = MutableStateFlow("")
    val pnrInput: StateFlow<String> = _pnrInput

    private val _pasteInput = MutableStateFlow("")
    val pasteInput: StateFlow<String> = _pasteInput

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _checkedPnr = MutableStateFlow<String?>(null)
    val checkedPnr: StateFlow<String?> = _checkedPnr

    fun updatePnrInput(value: String) {
        _pnrInput.value = PnrEngine.normalize(value)
        _error.value = null
    }

    fun updatePasteInput(value: String) {
        _pasteInput.value = value
    }

    fun applyPaste() {
        val parsed = PnrEngine.parseFromText(_pasteInput.value)
        if (parsed == null) {
            _error.value = "No valid 10-digit PNR found in pasted text"
        } else {
            _pnrInput.value = parsed
            _error.value = null
        }
    }

    fun checkPnr(): Boolean {
        val pnr = _pnrInput.value
        if (!PnrEngine.isValid(pnr)) {
            _error.value = "Enter a valid 10-digit PNR"
            return false
        }
        _checkedPnr.value = pnr
        _error.value = null
        return true
    }

    fun clearCheck() {
        _checkedPnr.value = null
    }

    fun saveCurrent(nickname: String) {
        val pnr = _checkedPnr.value ?: _pnrInput.value
        if (!PnrEngine.isValid(pnr)) {
            _error.value = "Enter a valid PNR before saving"
            return
        }
        viewModelScope.launch {
            savedPnrRepository.save(
                SavedPnr(
                    pnr = pnr,
                    nickname = nickname.trim().ifEmpty { "PNR $pnr" }
                )
            )
        }
    }

    fun saveEntry(entry: SavedPnr) {
        viewModelScope.launch { savedPnrRepository.save(entry) }
    }

    fun deleteSaved(pnr: String) {
        viewModelScope.launch { savedPnrRepository.delete(pnr) }
    }

    fun loadSaved(pnr: String) {
        _pnrInput.value = pnr
        _checkedPnr.value = pnr
        _error.value = null
    }

    fun setAds(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updatePreferences { it.copy(adsEnabled = enabled) }
        }
    }
}
