import Data.Map (Map)
import qualified Data.Map as Map

import AocUtil (solve)


main = do
    solve 5 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ls part =
    Map.size $ Map.filter (> 1) pointToCount
    where
        segments = map parseLine ls
        segmentsPart = filter (\((x1, y1), (x2, y2)) -> (part /= 1 || x1 == x2 || y1 == y2)) segments
        pointToCount = foldl incrementPoints Map.empty segmentsPart

parseLine :: String -> ((Int, Int), (Int, Int))
parseLine l =
    ((toPoint sStart), (toPoint sEnd))
    where
        ss = words l -- "x1,y1 -> x2,y2" -> ["x1,y1", "->", "x2,y2"]
        sStart = head ss
        sEnd = last ss
        toPoint s = let (left, right) = break (== ',') s in (read left :: Int, read (tail right) :: Int)

incrementPoints :: Map.Map (Int, Int) Int -> ((Int, Int), (Int, Int)) -> Map.Map (Int, Int) Int
incrementPoints mp ((x1, y1), (x2, y2)) =
    case compare x1 x2 of
        LT -> incrementPointsOrdered mp ((x1, y1), (x2, y2))
        EQ -> incrementPointsOrdered mp ((x1, (min y1 y2)), (x1, (max y1 y2)))
        GT -> incrementPointsOrdered mp ((x2, y2), (x1, y1))

incrementPointsOrdered :: Map.Map (Int, Int) Int -> ((Int, Int), (Int, Int)) -> Map.Map (Int, Int) Int
incrementPointsOrdered mp ((x1, y1), (x2, y2)) =
    let mpInc = Map.insertWith (+) (x1, y1) 1 mp
    in case (x1 == x2, y1 == y2) of
        (True, True) -> mpInc
        (True, False) -> incrementPointsOrdered mpInc ((x1, y1 + 1), (x2, y2))
        (False, True) -> incrementPointsOrdered mpInc ((x1 + 1, y1), (x2, y2))
        (False, False) -> if y1 < y2
                          then incrementPoints mpInc ((x1 + 1, y1 + 1), (x2, y2))
                          else incrementPoints mpInc ((x1 + 1, y1 - 1), (x2, y2))
