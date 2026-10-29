var form = document.getElementById("calcForm");

if (form) {
    form.addEventListener("submit", function (event) {
        event.preventDefault();

        var aStr = document.getElementById("a").value.trim().replace(",", ".");
        var bStr = document.getElementById("b").value.trim().replace(",", ".");
        var op = document.querySelector("input[name=op]:checked").value;

        if (aStr === "" || bStr === "") {
            alert("Заполните оба поля ввода!");
            return;
        }

        var a = Number(aStr);
        var b = Number(bStr);
        if (isNaN(a) || isNaN(b)) {
            alert("В полях ввода должны быть числа!");
            return;
        }

        if (op === "div" && b === 0) {
            alert("На ноль делить нельзя!");
            return;
        }

        window.location.href = "result.html?a=" + aStr + "&b=" + bStr + "&op=" + op;
    });
} else {
    var params = new URLSearchParams(window.location.search);
    var aStr = params.get("a");
    var bStr = params.get("b");
    var op = params.get("op");

    var argA = document.getElementById("argA");
    var argB = document.getElementById("argB");
    var opEl = document.getElementById("operation");
    var resEl = document.getElementById("resultValue");

    if (aStr === null || bStr === null || op === null) {
        resEl.textContent = "Ошибка: данные не переданы!";
        resEl.className = "error";
    } else {
        var a = parseFloat(aStr);
        var b = parseFloat(bStr);
        argA.textContent = aStr;
        argB.textContent = bStr;

        if (op === "div" && b === 0) {
            opEl.textContent = "Деление (÷)";
            resEl.textContent = "Ошибка: деление на ноль!";
            resEl.className = "error";
        } else {
            var result;
            if (op === "add") {
                opEl.textContent = "Сложение (+)";
                result = a + b;
            } else if (op === "sub") {
                opEl.textContent = "Вычитание (−)";
                result = a - b;
            } else if (op === "mul") {
                opEl.textContent = "Умножение (×)";
                result = a * b;
            } else if (op === "div") {
                opEl.textContent = "Деление (÷)";
                result = a / b;
            } else {
                resEl.textContent = "Ошибка: некорректные данные!";
                resEl.className = "error";
            }

            if (result !== undefined) {
                resEl.textContent = Math.round(result * 1e10) / 1e10;
            }
        }
    }
}
