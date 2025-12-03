require 'aoc'
require 'cs'

function get_answer(lines, part)
    local joltage = 0
    local n_batteries = part == 1 and 2 or 12
    for _, line in ipairs(lines) do
        -- Convert to numbers.
        local bank = {}
        for i = 1, string.len(line) do
            table.insert(bank, tonumber(string.sub(line, i, i)))
        end
        -- Successive-greedily find the max value for each battery slot; store the index.
        local indices_max = {}
        while #indices_max < n_batteries do
            local j_battery = #indices_max + 1
            local i_max = j_battery == 1 and 1 or indices_max[j_battery - 1] + 1
            for i = i_max + 1, #bank - n_batteries + j_battery do
                if bank[i] > bank[i_max] then
                    i_max = i
                end
            end
            indices_max[j_battery] = i_max
        end
        -- Map from indices to values as digits.
        local digits = {}
        for _, index in ipairs(indices_max) do
            table.insert(digits, bank[index])
        end
        -- Sum.
        joltage = joltage + from_digits(digits)
    end
    return joltage
end

solve(3, get_answer)
