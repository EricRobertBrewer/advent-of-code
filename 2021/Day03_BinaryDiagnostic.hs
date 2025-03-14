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

oneCount :: String -> Int
oneCount s =
    sum $ map readC s
    where readC c = read [c] :: Int

selectBits :: [Int] -> Int -> (Int -> Int -> Bool) -> [Int]
selectBits [] _ _ = []
selectBits (count:counts) n op = ((if (op count (n - count)) then 1 else 0):selectBits counts n op)

toDecimal :: [Int] -> Int
toDecimal xs =
    let digit = last xs
    in if null (init xs) then digit else 2 * toDecimal (init xs) + digit
