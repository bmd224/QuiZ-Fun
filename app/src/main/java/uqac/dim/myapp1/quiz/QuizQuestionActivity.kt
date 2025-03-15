package uqac.dim.myapp1.quiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import uqac.dim.myapp1.utils.FirebaseUtils
import uqac.dim.myapp1.R

class QuizQuestionActivity : AppCompatActivity() {

    // Références aux vues
    private lateinit var texteQuestion: TextView
    private lateinit var texteCompteurQuestion: TextView
    private lateinit var groupeOptions: RadioGroup
    private lateinit var option1: RadioButton
    private lateinit var option2: RadioButton
    private lateinit var option3: RadioButton
    private lateinit var option4: RadioButton
    private lateinit var boutonSuivant: Button
    private lateinit var barreProgression: ProgressBar
    private lateinit var texteChronometre: TextView

    // Données du quiz
    private var toutesLesQuestions: MutableList<Map<String, Any>> = mutableListOf()
    private var indexQuestionActuelle = 0
    private var score = 0
    private var chronometre: CountDownTimer? = null
    private val totalQuestions = 17

    private var tempsTotalEnSecondes = 0
    private var chronometreGlobal: CountDownTimer? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        // Initialisation des vues
        texteQuestion = findViewById(R.id.tvQuestion)
        texteCompteurQuestion = findViewById(R.id.tvQuestionCount)
        groupeOptions = findViewById(R.id.radioGroupOptions)
        option1 = findViewById(R.id.radioOption1)
        option2 = findViewById(R.id.radioOption2)
        option3 = findViewById(R.id.radioOption3)
        option4 = findViewById(R.id.radioOption4)
        boutonSuivant = findViewById(R.id.btnNext)
        barreProgression = findViewById(R.id.progressBarQuiz)
        texteChronometre = findViewById(R.id.tvTimer)

        barreProgression.max = totalQuestions
        barreProgression.progress = 0

        //Démarrer le chronomètre global dès la première question
        demarrerChronometreGlobal()

        //chargement des questions depuis Firestore
        FirebaseUtils.envoyerQuestionsVersFirestore(this) {
            FirebaseUtils.chargerQuestionsDepuisFirestore { quizzes ->
                if (quizzes.isNotEmpty()) {
                    toutesLesQuestions.clear()
                    quizzes.forEach { quiz ->
                        val listeQuestions = quiz["questionList"] as? List<Map<String, Any>> ?: emptyList()
                        toutesLesQuestions.addAll(listeQuestions)
                    }

                    if (toutesLesQuestions.size >= totalQuestions) {
                        indexQuestionActuelle = 0
                        mettreAJourUI()
                    } else {
                        Toast.makeText(this, "Nombre de questions insuffisant", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Aucun quiz trouvé", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        //bouton Next
        boutonSuivant.setOnClickListener {
            verifierReponseEtContinuer()
        }
    }

    //chronomètre global pour le temps total
    private fun demarrerChronometreGlobal() {
        chronometreGlobal?.cancel()
        chronometreGlobal = object : CountDownTimer(60 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tempsTotalEnSecondes++
            }
            override fun onFinish() {}
        }.start()
    }

    //Démarrer le chronomètre pour chaque question
    //je l'ai fixé à 30 secondes pour chaque question
    private fun demarrerChronometre() {
        chronometre?.cancel()
        chronometre = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                texteChronometre.text = "${millisUntilFinished / 1000} s"
            }

            override fun onFinish() {
                Toast.makeText(this@QuizQuestionActivity, "Temps écoulé ! Vous perdez des points.", Toast.LENGTH_SHORT).show()
                score = maxOf(0, score - 1)
                passerAQuestionSuivante()
            }
        }.start()
    }

    //verifier la reponse de l'utilisateur
    // Il faut obligatoirement choisir une reponse avant de passer à la question suivante meme si elle est fausse
    private fun verifierReponseEtContinuer() {
        val idSelectionne = groupeOptions.checkedRadioButtonId
        if (idSelectionne == -1) {
            Toast.makeText(this, "Veuillez sélectionner une réponse SVP", Toast.LENGTH_SHORT).show()
            return
        }

        val optionSelectionnee = findViewById<RadioButton>(idSelectionne)
        val reponseSelectionnee = optionSelectionnee.text.toString()

        if (indexQuestionActuelle >= toutesLesQuestions.size) {
            finish()
            return
        }

        val questionActuelle = toutesLesQuestions[indexQuestionActuelle]
        val reponseCorrecte = questionActuelle["correct"].toString()

        // Comparer la réponse sélectionnée avec la réponse correcte
        if (reponseSelectionnee == reponseCorrecte) {
            score++
            Toast.makeText(this, "Bonne réponse !", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Mauvaise réponse. La bonne réponse est $reponseCorrecte", Toast.LENGTH_SHORT).show()
        }

        passerAQuestionSuivante()
    }

    // Passer à la question suivante
    private fun passerAQuestionSuivante() {
        chronometre?.cancel()

        // Vérifier si c'était la dernière question
        if (indexQuestionActuelle == totalQuestions - 1) {
            chronometreGlobal?.cancel()
            val tempsTotal = tempsTotalEnSecondes

            // Affichage du résultat
            setContent {
                QuizResultScreen(
                    reponsesCorrectes = score,
                    totalQuestions = totalQuestions,
                    tempsTotal = tempsTotal
                ) {
                    finish() // Fermer QuizResultScreen(cette activite) quand l'utilisateur clique sur "Terminer"
                }
            }
            return
        }

        // Passer à la question suivante
        indexQuestionActuelle++
        barreProgression.progress = indexQuestionActuelle + 1
        texteCompteurQuestion.text = "Question ${indexQuestionActuelle + 1}/$totalQuestions"

        mettreAJourUI()
    }

    // Mettre à jour l'interface utilisateur
    private fun mettreAJourUI() {
        if (indexQuestionActuelle < toutesLesQuestions.size) {
            val questionActuelle = toutesLesQuestions[indexQuestionActuelle]
            texteQuestion.text = questionActuelle["question"].toString()

            val options = questionActuelle["options"] as? List<*>
            if (options != null && options.size >= 4) {
                option1.text = options[0].toString()
                option2.text = options[1].toString()
                option3.text = options[2].toString()
                option4.text = options[3].toString()
            }
            //reinitialisation des choix de reponse
            groupeOptions.clearCheck()

            //redemarrer le timer pour la nouvelle question
            demarrerChronometre()
        }
    }
}