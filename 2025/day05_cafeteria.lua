require 'aoc'
require 'cs'

function get_answer(lines, part)
    local i_blank = -1
    for i, line in ipairs(lines) do
        if line == '' then
            if i_blank ~= -1 then
                error('Two blank lines found: `' .. i_blank .. '` and `' .. i .. '`')
            end
            i_blank = i
        end
    end
    if i_blank == -1 then
        error('No blank line found.')
    end

    local ranges_orig = {}
    for i = 1, i_blank - 1 do
        local lo_hi_strs = split_char(lines[i], '-')
        local lo = tonumber(lo_hi_strs[1])
        local hi = tonumber(lo_hi_strs[2])
        ranges_orig[i] = lo < hi and {lo, hi} or {hi, lo}
    end

    local ingredients = {}
    for i = i_blank + 1, #lines do
        ingredients[tonumber(lines[i])] = true -- As a set.
    end

    -- Sort overlapping ranges.
    local ranges = {}
    for _, range_orig in ipairs(ranges_orig) do
        -- Move to relevant starting position.
        local j = 1
        while j <= #ranges and range_orig[1] > ranges[j][2] + 1 do
            j = j + 1
        end
        -- Store smaller starting value of ranges.
        if j <= #ranges then
            range_orig[1] = math.min(ranges[j][1], range_orig[1])
        end
        -- Delete ranges which the target range consumes.
        while j <= #ranges and range_orig[2] > ranges[j][2] do
            table.remove(ranges, j)
        end
        if j > #ranges then
            -- Append.
            table.insert(ranges, range_orig)
        else
            if range_orig[2] < ranges[j][1] - 1 then
                -- No overlap; insert.
                table.insert(ranges, j, range_orig)
            else
                -- Overlap; modify.
                ranges[j][1] = range_orig[1]
                ranges[j][2] = math.max(ranges[j][2], range_orig[2])
            end
        end
    end

    if part == 1 then
        local n_fresh = 0
        for ingredient in pairs(ingredients) do
            local is_fresh = false
            for _, range in ipairs(ranges) do
                local low = range[1]
                local high = range[2]
                if low <= ingredient and ingredient <= high then
                    is_fresh = true
                    break
                end
            end
            if is_fresh then n_fresh = n_fresh + 1 end
        end
        return n_fresh
    end

    local n_total = 0
    for _, range in ipairs(ranges) do
        n_total = n_total + range[2] - range[1] + 1
    end
    return n_total
end

solve(5, get_answer)
