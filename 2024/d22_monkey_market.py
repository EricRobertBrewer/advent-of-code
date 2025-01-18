from collections import defaultdict

import aoc


def get_answer(lines, part):
    secrets = list(map(int, lines))

    if part == 1:
        answer = 0
        for secret in secrets:
            for _ in range(2000):
                secret = get_next_secret(secret)
            answer += secret
        return answer

    sequence_to_total = defaultdict(int)
    for secret in secrets:
        sequence_to_price = dict()
        changes = list()
        price_prev = secret % 10
        for _ in range(2000):
            secret = get_next_secret(secret)
            price = secret % 10
            changes.append(price - price_prev)
            if len(changes) >= 4:
                sequence = tuple(changes[-4:])
                if sequence not in sequence_to_price:
                    sequence_to_price[sequence] = price
            price_prev = price

        for sequence, price in sequence_to_price.items():
            sequence_to_total[sequence] += price
    return max(sequence_to_total.values())


def get_next_secret(secret):
    secret = ((secret * 64) ^ secret) % 16777216
    secret = ((secret // 32) ^ secret) % 16777216
    secret = ((secret * 2048) ^ secret) % 16777216
    return secret


if __name__ == '__main__':
    aoc.solve(22, get_answer)
