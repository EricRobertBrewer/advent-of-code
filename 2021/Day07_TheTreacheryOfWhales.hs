import AocUtil (solve)
import Cs (splitOnString)


main = do
    solve 7 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ls part =
    minimum $ map alignCost [crabMin..crabMax]
    where
        crabs = map (\s -> read s :: Int) $ splitOnString "," (head ls)
        crabMin = minimum crabs
        crabMax = maximum crabs
        fCost = if part == 1 then (\x -> x) else (\x -> x * (x + 1) `div` 2) -- triangle
        alignCost x = sum $ map (\crab -> fCost (abs (crab - x))) crabs
