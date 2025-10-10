import Data.Map (Map)
import qualified Data.Map as Map
import qualified Data.Text as T

import AocUtil (solve)

main = do
    solve 6 getAnswer

getAnswer :: [String] -> Int -> Int
getAnswer ls part =
    Map.foldl (+) 0 $ processDays timerToCount 0 dayEnd
    where
        inputFish = map (\x -> read x :: Int) $ map T.unpack $ T.splitOn (T.pack ",") $ T.pack $ head ls
        timerToCount = foldl (\mp x -> Map.insertWith (+) x 1 mp) Map.empty inputFish
        dayEnd = if part == 1 then 80 else 256

processDays :: Map.Map Int Int -> Int -> Int -> Map.Map Int Int
processDays mp day dayEnd
    | day == dayEnd = mp
    | day < dayEnd = processDays (processDay mp) (day + 1) dayEnd

processDay :: Map.Map Int Int -> Map.Map Int Int
processDay mp =
    Map.foldlWithKey tickDownTimers mpInit mpPositive
    where
        tickDownTimers mpNext timer count = Map.insertWith (+) (timer - 1) count mpNext
        mpInit = Map.fromList [(8, Map.findWithDefault 0 0 mp), (6, Map.findWithDefault 0 0 mp)]
        mpPositive = Map.filterWithKey (\timer _ -> timer > 0) mp
