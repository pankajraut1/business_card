import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

object CardStorageHelper {

    private const val PREFS_NAME = "SavedCardsPrefs"
    private const val CARDS_KEY = "saved_cards"

    fun saveCard(context: Context, cardData: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existingCards = getSavedCards(context).toMutableList()
        existingCards.add(cardData)

        val jsonArray = JSONArray()
        for (card in existingCards) {
            jsonArray.put(card)
        }

        prefs.edit().putString(CARDS_KEY, jsonArray.toString()).apply()
    }

    fun getSavedCards(context: Context): List<String> {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(CARDS_KEY, "[]")
        val jsonArray = JSONArray(json)
        val cardList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            cardList.add(jsonArray.getString(i))
        }
        return cardList
    }
}
