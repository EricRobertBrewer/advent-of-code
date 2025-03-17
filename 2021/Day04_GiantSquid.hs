import Data.List (partition, transpose)

import AocUtil (solve)
import Cs (groupAt)

main = do
    solve 4 getAnswer

size = 5

getAnswer :: [String] -> Int -> Int
getAnswer ss part =
    drawWin * (sumUnmarked boardWin)
    where
        draws = read ("[" ++ (head ss) ++ "]") :: [Int]
        boards = parseBoards $ tail ss
        (drawWin, boardWin) = drawAndMarkBoards (part == 1) draws boards

parseBoards :: [String] -> [[[Int]]]
parseBoards ss =
    map parseBoard $ map tail $ groupAt (size + 1) ss
    where parseBoard ls = map (\l -> map read (groupAt 3 (" " ++ l))) ls

drawAndMarkBoards :: Bool -> [Int] -> [[[Int]]] -> (Int, [[Int]])
drawAndMarkBoards returnFirst (draw:draws) boards =
    if (returnFirst && not (null boardsWin)) || (not returnFirst && (null boardsActive))
    then (draw, head boardsWin)
    else drawAndMarkBoards returnFirst draws boardsActive
    where
        boardsMarked = markBoards draw boards
        (boardsWin, boardsActive) = partition isWin boardsMarked

markBoards :: Int -> [[[Int]]] -> [[[Int]]]
markBoards draw boards = map (markBoard draw) boards

markBoard :: Int -> [[Int]] -> [[Int]]
markBoard draw board = map (\xs -> map (\x -> if x == draw then (-x) else x) xs) board

isWin :: [[Int]] -> Bool
isWin board = (any (\xs -> all (<0) xs) board) || (any (\xs -> all (<0) xs) (transpose board))

sumUnmarked :: [[Int]] -> Int
sumUnmarked board = sum $ map (\xs -> sum $ filter (>0) xs) board

