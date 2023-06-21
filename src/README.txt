- Command-line compile.  In the connectx/ directory run::

		javac -cp ".." *.java */*.java


CXGame application:

- Human vs Computer.  In the connectx/ directory run:
	
		java -cp ".." connectx.CXGame 6 7 4 connectx.L0.L0


- Computer vs Computer. In the connectx/ directory run:

		java -cp ".." connectx.CXGame 6 7 4 connectx.L0.L0 connectx.L1.L1


- Verbose output and customized timeout (1 sec) and number of game repetitions (10 rounds)

	java -cp ".." connectx.CXGame 6 7 4 connectx.AlphabetCXPlayer.AlphabetCXPlayer connectx.L1.L1 -v -t 1 -r 10