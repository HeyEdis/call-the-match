const stadiumSelect = document.getElementById("stadium");
const stadiumCodeInput = document.getElementById("stadiumCode");
const checksumInput = document.getElementById("checksum");

stadiumSelect.addEventListener("change", () => {
    const stadiumCode = stadiumSelect.selectedOptions[0].dataset.code;
    stadiumCodeInput.value = stadiumCode ?? "";
    checksumInput.value = stadiumCode ? Number.parseInt(stadiumCode, 10) % 97 : "";
});
