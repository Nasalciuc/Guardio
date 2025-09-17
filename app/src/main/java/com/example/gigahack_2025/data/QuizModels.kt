package com.example.gigahack_2025.data

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class QuizResult(
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val message: String
)

fun getCybersecurityQuizQuestions(): List<QuizQuestion> {
    return listOf(
        QuizQuestion(
            id = 1,
            question = "Care este cel mai sigur mod de a crea o parolă?",
            options = listOf(
                "Folosește numele animalului tău de companie",
                "Folosește o combinație de litere mari, litere mici, cifre și simboluri",
                "Folosește data nașterii"
            ),
            correctAnswerIndex = 1,
            explanation = "Parolele puternice ar trebui să includă un amestec de litere mari, litere mici, cifre și simboluri speciale pentru a fi mai greu de ghicit sau spart."
        ),
        QuizQuestion(
            id = 2,
            question = "Ce ar trebui să faci dacă primești un email suspect care cere informații personale?",
            options = listOf(
                "Răspunde cu informațiile solicitate",
                "Șterge emailul și raportează-l ca spam",
                "Redirecționează-l tuturor contactelor"
            ),
            correctAnswerIndex = 1,
            explanation = "Emailurile suspecte care cer informații personale sunt probabil tentative de phishing. Șterge-le și raportează-le ca spam pentru a te proteja pe tine și pe alții."
        ),
        QuizQuestion(
            id = 3,
            question = "Ce este autentificarea cu doi factori (2FA)?",
            options = listOf(
                "Folosirea a două parole diferite",
                "O metodă de securitate care necesită două forme de verificare",
                "A avea două conturi de email"
            ),
            correctAnswerIndex = 1,
            explanation = "Autentificarea cu doi factori adaugă un strat suplimentar de securitate solicitând ceva ce știi (parola) și ceva ce ai (telefonul, aplicația de autentificare)."
        ),
        QuizQuestion(
            id = 4,
            question = "Ce ar trebui să faci înainte de a da clic pe un link dintr-un email?",
            options = listOf(
                "Dă clic imediat dacă pare interesant",
                "Trece cursorul peste link pentru a vedea URL-ul real",
                "Redirecționează emailul prietenilor"
            ),
            correctAnswerIndex = 1,
            explanation = "Treci întotdeauna cursorul peste link pentru a verifica URL-ul real al destinației înainte de a da clic. Acest lucru ajută la identificarea tentativelor de phishing și a site-urilor malițioase."
        ),
        QuizQuestion(
            id = 5,
            question = "Care este cea mai bună practică pentru utilizarea Wi‑Fi-ului public?",
            options = listOf(
                "Folosește-l pentru toate activitățile online",
                "Evită accesarea conturilor sensibile și folosește un VPN",
                "Distribuie liber informațiile personale"
            ),
            correctAnswerIndex = 1,
            explanation = "Rețelele Wi‑Fi publice sunt adesea nesecurizate. Evită accesarea conturilor sensibile și folosește un VPN pentru a-ți cripta conexiunea când este necesar."
        ),
        QuizQuestion(
            id = 6,
            question = "Ce ar trebui să faci dacă computerul tău arată semne de virus?",
            options = listOf(
                "Ignoră și continuă să folosești computerul",
                "Deconectează-te de la internet și rulează un antivirus",
                "Distribuie fișierele altora ca să îi ajuți"
            ),
            correctAnswerIndex = 1,
            explanation = "Dacă suspectezi un virus, deconectează-te imediat de la internet pentru a preveni răspândirea și rulează software-ul antivirus pentru a scana și elimina amenințările."
        ),
        QuizQuestion(
            id = 7,
            question = "Care este un mod sigur de a stoca parole importante?",
            options = listOf(
                "Notează-le pe bilețele",
                "Folosește un manager de parole de încredere",
                "Salvează-le într-un fișier text pe desktop"
            ),
            correctAnswerIndex = 1,
            explanation = "Managerii de parole criptează și stochează în siguranță parolele, făcându-le accesibile doar ție și protejându-le de hackeri."
        ),
        QuizQuestion(
            id = 8,
            question = "Ce ar trebui să faci dacă un site îți cere CNP-ul?",
            options = listOf(
                "Oferă-l imediat",
                "Verifică legitimitatea site-ului și furnizează-l doar dacă este absolut necesar",
                "Distribuie-l pe rețelele sociale"
            ),
            correctAnswerIndex = 1,
            explanation = "CNP-urile sunt foarte sensibile. Oferă-le numai organizațiilor verificate și legitime și doar când este absolut necesar."
        ),
        QuizQuestion(
            id = 9,
            question = "Care este scopul actualizărilor de software?",
            options = listOf(
                "Să încetinească computerul",
                "Să remedieze vulnerabilități de securitate și să îmbunătățească performanța",
                "Să folosească mai mult spațiu de stocare"
            ),
            correctAnswerIndex = 1,
            explanation = "Actualizările de software includ adesea patch-uri de securitate care repară vulnerabilități descoperite de dezvoltatori, ajutând la protejarea dispozitivului de amenințări cibernetice."
        ),
        QuizQuestion(
            id = 10,
            question = "Ce ar trebui să faci dacă descarci din greșeală un program malware?",
            options = listOf(
                "Rulează fișierul descărcat",
                "Șterge imediat fișierul și rulează o scanare completă a sistemului",
                "Distribuie-l prietenilor"
            ),
            correctAnswerIndex = 1,
            explanation = "Dacă descarci din greșeală malware, șterge imediat fișierul și rulează o scanare completă cu antivirusul pentru a elimina orice amenințare."
        )
    )
}

fun getQuizResultMessage(score: Int, totalQuestions: Int): String {
    val percentage = (score * 100) / totalQuestions
    
    return when {
        percentage >= 90 -> "Excelent! Ești expert în securitate cibernetică! 🛡️ Ai cunoștințe excelente despre practicile de siguranță online. Ține-o tot așa!"
        percentage >= 80 -> "Foarte bine! Ai un nivel ridicat de conștientizare în securitate! 🔒 Înțelegi bine majoritatea conceptelor. Continuă să înveți pentru a rămâne protejat!"
        percentage >= 70 -> "Bună treabă! Ai o bază solidă în securitate cibernetică! 🛡️ Cunoști bine elementele de bază. Ia în considerare să înveți mai multe practici avansate."
        percentage >= 60 -> "Nu e rău! Ai unele cunoștințe de securitate cibernetică! 🔐 Înțelegi anumite concepte. Continuă să înveți pentru a-ți îmbunătăți siguranța online!"
        percentage >= 50 -> "Este loc de îmbunătățire! Ai cunoștințe de bază despre securitate! 📚 Cunoști unele noțiuni de bază. Concentrează-te pe a învăța mai multe despre siguranța online."
        else -> "Continuă să înveți! Securitatea cibernetică este importantă pentru toată lumea! 📖 Există unele lacune de cunoștințe. Alocă timp pentru a învăța despre siguranța online ca să te protejezi pe tine și pe ceilalți."
    }
}
