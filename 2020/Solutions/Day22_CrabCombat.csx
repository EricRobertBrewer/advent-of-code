#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day22_CrabCombat : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        Queue<int> deck1 = new Queue<int>();
        Queue<int> deck2 = new Queue<int>();
        int indexBlank = -1;
        for (int i = 0; i < lines.Count; i++) {
            string line = lines[i];
            if (i == 0) {
                if (line != "Player 1:") {
                    throw new SystemException("Expected 'Player 1:' on line 0.");
                }
            } else if (i == indexBlank + 1) {
                if (line != "Player 2:") {
                    throw new SystemException("Expected 'Player 2:' on line " + (indexBlank + 1) + ".");
                }
            } else if (line.Length == 0) {
                if (indexBlank != -1) {
                    throw new SystemException("Multiple blank lines: " + indexBlank + " and " + i);
                }
                indexBlank = i;
            } else {
                int card = Int32.Parse(line);
                if (indexBlank == -1) {
                    deck1.Enqueue(card);
                } else {
                    deck2.Enqueue(card);
                }
            }
        }
        if (indexBlank == -1) {
            throw new SystemException("Unable to find blank line.");
        }

        Queue<int> deck;
        if (part == 1) {
            while (deck1.Count > 0 && deck2.Count > 0) {
                int card1 = deck1.Dequeue();
                int card2 = deck2.Dequeue();
                if (card1 > card2) {
                    deck1.Enqueue(card1);
                    deck1.Enqueue(card2);
                } else {
                    deck2.Enqueue(card2);
                    deck2.Enqueue(card1);
                }
            }
            deck = (deck1.Count > 0) ? deck1 : deck2;
        } else {
            int winner = RecursiveCombat(deck1, deck2);
            deck = (winner == 1) ? deck1 : deck2;
        }

        long score = 0L;
        while (deck.Count > 0) {
            int count = deck.Count;
            int card = deck.Dequeue();
            score += card * count;
        }
        return score;
    }

    private static int RecursiveCombat(Queue<int> deck1, Queue<int> deck2) {
        var states = new HashSet<string>();
        while (deck1.Count > 0 && deck2.Count > 0) {
            string state = String.Join(",", deck1) + "|" + String.Join(",", deck2);
            if (states.Contains(state)) {
                return 1;
            }
            states.Add(state);

            int card1 = deck1.Dequeue();
            int card2 = deck2.Dequeue();
            int winner;
            if (deck1.Count >= card1 && deck2.Count >= card2) {
                Queue<int> deck1R = new Queue<int>();
                Queue<int> deck2R = new Queue<int>();
                foreach (int card in deck1) {
                    if (deck1R.Count == card1) {
                        break;
                    }
                    deck1R.Enqueue(card);
                }
                foreach (int card in deck2) {
                    if (deck2R.Count == card2) {
                        break;
                    }
                    deck2R.Enqueue(card);
                }
                winner = RecursiveCombat(deck1R, deck2R);
            } else {
                winner = (card1 > card2) ? 1 : 2;
            }

            if (winner == 1) {
                deck1.Enqueue(card1);
                deck1.Enqueue(card2);
            } else {
                deck2.Enqueue(card2);
                deck2.Enqueue(card1);
            }
        }

        return (deck1.Count > 0) ? 1 : 2;
    }
}

AocUtil.RunSolver(22, Args, new Day22_CrabCombat());
