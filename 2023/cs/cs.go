package cs

func GCD(a, b int) int {
    // Euclidean algorithm.
    for a != b {
        if a > b {
            a -= b
        } else {
            b -= a
        }
    }
    return a
}

func LCM(arr ...int) int {
    if len(arr) < 1 {
        return 0
    }
    y := arr[0]
    for _, x := range arr[1:] {
        y = y * x / GCD(y, x)
    }
    return y
}