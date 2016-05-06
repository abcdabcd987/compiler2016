void foo(int d, int x, int y) {
    println(toString(d*1000 + x*10 + y));
    if (d == 1) return;
    int t = x; x = y; y = t;
    foo(1, x, y);
    println(toString(d*1000 + x*10 + y));
}
int main() {
    foo(7, 5, 3);
}