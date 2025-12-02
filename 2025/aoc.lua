function solve(day, get_answer)
    local part_str = assert(arg[1], 'Usage: $ lua day##_XXXX.lua <part>')
    local part = tonumber(part_str)

    local fname = tostring(day) .. '.txt'
    if day < 10 then
        fname = '0' .. fname
    end
    io.input('../input/2025/' .. fname)

    local start = os.clock()
    local answer = get_answer(io.lines(), part)
    local duration = os.clock() - start
    print(answer)
    print('Time: ' .. duration .. ' s')
end
