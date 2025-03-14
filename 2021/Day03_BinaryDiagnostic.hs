import Data.List (transpose)

import AocUtil (solve)

main = do
    solve 3 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ss 1 =
    gamma * epsilon
    where
        columnOneCounts = map oneCount $ transpose ss
        n = length ss
        gamma = toDecimal $ selectBits columnOneCounts n (>)
        epsilon = toDecimal $ selectBits columnOneCounts n (<)
getAnswer ss 2 =
    oxygen * co2
    where
        oxygen = toDecimal $ criteriaFilter ss (>=)
        co2 = toDecimal $ criteriaFilter ss (<)

oneCount :: String -> Int
oneCount s = sum $ map readC s

readC :: Char -> Int
readC c = read [c] :: Int

selectBits :: [Int] -> Int -> (Int -> Int -> Bool) -> [Int]
selectBits [] _ _ = []
selectBits (count:counts) n op = ((if (op count (n - count)) then 1 else 0):selectBits counts n op)

toDecimal :: [Int] -> Int
toDecimal xs =
    let digit = last xs
    in if null (init xs) then digit else 2 * toDecimal (init xs) + digit

criteriaFilter :: [String] -> (Int -> Int -> Bool) -> [Int]
criteriaFilter ss op =
    case length ss of
        1 -> map readC $ head ss
        _ -> let (ss0, ss1) = binaryPartition ss
             in if (op (length ss1) (length ss0))
                then (1:criteriaFilter (map tail ss1) op)
                else (0:criteriaFilter (map tail ss0) op)

binaryPartition :: [String] -> ([String], [String])
binaryPartition [] = ([], [])
binaryPartition (s:ss) =
    let (ss0, ss1) = binaryPartition ss
    in case head s of
       '0' -> ((s:ss0), ss1)
       '1' -> (ss0, (s:ss1))
       _ -> undefined
