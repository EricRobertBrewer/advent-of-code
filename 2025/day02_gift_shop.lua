require 'aoc'
require 'cs'

function get_answer(lines, part)
    local ranges = {}
    local range_ss = split_char(lines[1], ',')
    for i, range_s in ipairs(range_ss) do
        local range_parts = split_char(range_s, '-')
        if #range_parts ~= 2 then
            error('Unexpected range: ' .. range_s)
        end
        local low = tonumber(range_parts[1])
        local high = tonumber(range_parts[2])
        table.insert(ranges, {low, high})
    end

    local sum_invalid = 0
    local f_is_valid = is_valid_1
    if part == 2 then f_is_valid = is_valid_2 end
    for i, range in ipairs(ranges) do
        for j = range[1], range[2] do
            if not f_is_valid(j) then
                sum_invalid = sum_invalid + j
            end
        end
    end
    return sum_invalid
end

function is_valid_1(x)
    local s = tostring(x)
    local n = string.len(s)
    if n % 2 == 1 then return true end
    return string.sub(s, 1, n // 2) ~= string.sub(s, n // 2 + 1)
end

function is_valid_2(x)
    local s = tostring(x)
    local n = string.len(s)
    for length = 1, n // 2 do
        if n % length == 0 then
            local is_invalid = true
            local sub = string.sub(s, 1, length)
            for i = length + 1, n - length + 1, length do
                is_invalid = is_invalid and string.sub(s, i, i + length - 1) == sub
            end
            if is_invalid then return false end
        end
    end
    return true
end

solve(2, get_answer)
