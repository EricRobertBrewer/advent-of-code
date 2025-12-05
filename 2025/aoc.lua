function solve(day, get_answer)
    local part_str = assert(arg[1], 'Usage: $ lua day##_XXXX.lua <part> [--example|-x]')
    local part = tonumber(part_str)

    local example_str = arg[2]
    local use_example = example_str == '--example' or example_str == '-x'

    local fname = tostring(day)
    if day < 10 then fname = '0' .. fname end
    if use_example then fname = fname .. '-example' end
    fname = fname .. '.txt'

    local input_prev = io.input()
    io.input('../input/2025/' .. fname)
    local lines = {}
    for line in io.lines() do
        table.insert(lines, line)
    end
    io.input(input_prev)

    local start = os.clock()
    local answer = get_answer(lines, part)
    local duration = os.clock() - start
    print(answer)
    print('Time: ' .. duration .. ' s')
end
