package connectx;

public interface CXPlayer {
    //Inizializza il giocatore software
    // M = numero di righe nella matrice
    // N = numero di colonne nella matrice
    // X = numero di gettoni da allineare
    // first = true se e` il primo a giocare
    // timeout_in_secs = numero massimo di secondi per una mossa

    //Инициализируем программный плеер
    // M = количество строк в матрице
    // N = количество столбцов в матрице
    // X = количество жетонов для выстраивания
    // first = true, если играть первым
    // timeout_in_secs = максимальное количество секунд для хода
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs);

    // Seleziona una colonna tra quelle ancora libere
    // B = oggetto matrice di gioco

    // Выбираем столбец среди еще свободных
    // B = объект игровой матрицы
    public int selectColumn(CXBoard B);

    // Ritorna il nome del giocatore

    // Возвращает имя игрока
    public String playerName();
}