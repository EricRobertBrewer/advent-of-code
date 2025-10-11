module AocUtil (solve) where

import System.Environment (getArgs, getProgName)


solve :: Int -> ([String] -> Int -> Int) -> IO ()
solve day solver = do
    args <- getArgs
    case args of
        [arg0, "--example"] -> readInputAndGetAnswer day (read arg0) solver True
        [arg0, "-x"] ->        readInputAndGetAnswer day (read arg0) solver True
        [arg0] ->              readInputAndGetAnswer day (read arg0) solver False
        _ -> printUsage

readInputAndGetAnswer :: Int -> Int -> ([String] -> Int -> Int) -> Bool -> IO ()
readInputAndGetAnswer day part solver example = do
    contents <- readFile ("../input/2021/" ++ (frontPad 2 '0' day) ++ suffix ++ ".txt")
    putStrLn (show (solver (lines contents) part))
    where suffix = if example then "-example" else ""

-- Pad with '0's for single digit days, e.g., 3 -> "03"
frontPad :: Int -> Char -> Int -> String
frontPad len c x =
    let s = show x
    in replicate (len - length s) c ++ s

printUsage :: IO ()
printUsage = do
    prog <- getProgName
    putStrLn ("Usage: " ++ prog ++ " <part:[1|2]>")
