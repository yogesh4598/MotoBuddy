<?php
require_once __DIR__ . "/../../../config/db.php";

$data = json_decode(file_get_contents("php://input"));

if (!isset($conn) || $conn === null) {
    echo json_encode(["success" => false, "message" => "Database not connected. Please start MySQL in XAMPP."]);
    exit();
}

if (!empty($data->email) && !empty($data->password)) {
    $stmt = $conn->prepare("SELECT * FROM users WHERE email = :email");
    $stmt->bindParam(":email", $data->email);
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        if (password_verify($data->password, $user['password']) || $data->password === $user['password']) {
            $token = bin2hex(random_bytes(32));
            $updateStmt = $conn->prepare("UPDATE users SET token = :token WHERE id = :id");
            $updateStmt->bindParam(":token", $token);
            $updateStmt->bindParam(":id", $user['id']);
            $updateStmt->execute();

            echo json_encode([
                "success" => true,
                "message" => "Login successful",
                "data" => [
                    "token" => $token,
                    "user" => [
                        "id" => (string)$user['id'],
                        "name" => $user['name'],
                        "email" => $user['email']
                    ]
                ]
            ]);
            exit();
        }
    }
    http_response_code(401);
    echo json_encode(["success" => false, "message" => "Invalid email or password"]);
} else {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Missing email or password"]);
}
?>
